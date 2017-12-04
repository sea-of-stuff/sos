package uk.ac.standrews.cs.sos.impl.services;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;
import uk.ac.standrews.cs.sos.impl.context.ContextBuilder;
import uk.ac.standrews.cs.sos.impl.context.ContextManifest;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextVersionInfo;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContentsDirectoryFactory;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContentsDirectoryType;
import uk.ac.standrews.cs.sos.impl.context.examples.ReferencePredicate;
import uk.ac.standrews.cs.sos.impl.datamodel.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestParam;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.interfaces.context.ContextsContentsDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;

/**
 * The SOSContextService managed the contexts for this node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSContextService implements ContextService {

    // Resources and services
    private LocalStorage localStorage;
    private ManifestsDataService manifestsDataService;

    private CommonUtilities commonUtilities;

    // DATA STRUCTURES
    private ContextsContentsDirectory contextsContentsDirectory;
    private HashMap<IGUID, ComputationalUnit> cachedComputationalUnits;

    // This executor service will be used to schedule any background tasks
    private static final int CMS_SCHEDULER_PS = 4;
    private ScheduledExecutorService service;
    private Queue<Pair<Long, Long>> predicateThreadSessionStatistics;
    private Queue<Pair<Long, Long>> applyPolicyThreadSessionStatistics;
    private Queue<Pair<Long, Long>> checkPolicyThreadSessionStatistics;

    private long pred_time_prep;
    private long pred_time_to_check_if_predicate_has_to_be_run;
    private long pred_time_to_run_predicate_on_current_dataset;
    private long pred_time_to_update_context;
    private long policy_time_to_run_apply_on_current_dataset;
    private long policy_time_to_run_check_on_current_dataset;

    /**
     * Build a CMS instance.
     * The DDS is passed as parameter and it is needed to access the manifests to be processed.
     *
     * @param localStorage used to persist the internal data structures
     * @param manifestsDataService used to discover the data to process and act upon it
     * @param commonUtilities used to facilitate operations for the contexts
     */
    public SOSContextService(LocalStorage localStorage, ManifestsDataService manifestsDataService, CommonUtilities commonUtilities) throws ServiceException {

        try {
            this.localStorage = localStorage;
            this.manifestsDataService = manifestsDataService;
            this.commonUtilities = commonUtilities;

            contextsContentsDirectory = new ContextsContentsDirectoryFactory().makeContextsContentsDirectory(ContextsContentsDirectoryType.IN_MEMORY, localStorage);
            cachedComputationalUnits = new LinkedHashMap<>();

            predicateThreadSessionStatistics = new LinkedList<>();
            applyPolicyThreadSessionStatistics = new LinkedList<>();
            checkPolicyThreadSessionStatistics = new LinkedList<>();

            // Run background CRON Jobs if and only if this is set in the node settings file
            if (SOSLocalNode.settings.getServices().getCms().isAutomatic()) {

                service = new ScheduledThreadPoolExecutor(CMS_SCHEDULER_PS);
                runPredicatesPeriodic();
                runPoliciesPeriodic();
                checkPoliciesPeriodic();
                spawnContextsOverDomainPeriodic();
            } else {
                service = new ScheduledThreadPoolExecutor(1); // ThreadPool for triggered requests only
            }

        } catch (ContextException e) {
            throw new ServiceException(ServiceException.SERVICE.CONTEXT, e);
        }
    }

    @Override
    public Set<Context> getContexts() {

        Set<Context> contexts = new LinkedHashSet<>();
        for(IGUID contextRef:getContextsRefs()) {

            try {
                Context context = getContext(contextRef);
                contexts.add(context);
            } catch (ContextNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get context with ref: " + contextRef.toMultiHash());
                /* SKIP */
            }
        }

        return contexts;
    }

    @Override
    public Set<IGUID> getContextsRefs() {

        Set<IGUID> contexts = new LinkedHashSet<>();

        Set<IGUID> contextInvariants = manifestsDataService.getManifests(ManifestType.CONTEXT);
        for(IGUID contextInvariant:contextInvariants) {
            try {
                Iterator<IGUID> tips = manifestsDataService.getTips(contextInvariant).iterator();
                if (tips.hasNext()) {

                    IGUID contextTip = tips.next();
                    contexts.add(contextTip);
                }

            } catch (TIPNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get context tip from invariant ref: " + contextInvariant.toMultiHash());
                /* SKIP */
            }
        }

        // Shuffling contexts to avoid executing them always in the same order
        List<IGUID> temp = new ArrayList<>(contexts);
        Collections.shuffle(temp);
        return new LinkedHashSet<>(temp);
    }

    @Override
    public IGUID addContext(Context context) throws ContextException {

        try {
            manifestsDataService.addManifest(context);

            // Trigger context's predicate just after adding the context to the node based on the node settings
            if (SOSLocalNode.settings.getServices().getCms().isPredicateOnNewContext()) {
                runContextPredicateNow(context);
            }

            return context.guid();

        } catch (ManifestPersistException e) {
            throw new ContextException("Unable to add context to SOS node");
        }
    }

    @Override
    public IGUID addContext(ContextBuilder contextBuilder) throws ContextException {

        try {
            IGUID predicate = addPredicate(contextBuilder.predicate());
            Set<IGUID> policies = new LinkedHashSet<>();
            JsonNode policies_n = contextBuilder.policies();
            for (JsonNode policy_n : policies_n) {
                IGUID policy = addPolicy(policy_n);
                policies.add(policy);
            }

            JsonNode context_n = contextBuilder.context(predicate, policies);
            Context context = JSONHelper.JsonObjMapper().convertValue(context_n, Context.class);

            manifestsDataService.addManifest(contextBuilder.getCompoundManifest());
            addContext(context);
            return context.guid();

        } catch (ManifestPersistException | IOException e) {
            throw new ContextException("Unable to add context to SOS node");
        }
    }

    private IGUID addPredicate(JsonNode jsonNode) throws IOException, ManifestPersistException {

        Predicate predicate = JSONHelper.JsonObjMapper().convertValue(jsonNode, Predicate.class);
        manifestsDataService.addManifest(predicate);

        return predicate.guid();
    }

    private IGUID addPolicy(JsonNode jsonNode) throws IOException, ManifestPersistException {

        Policy policy = JSONHelper.JsonObjMapper().convertValue(jsonNode, Policy.class);
        manifestsDataService.addManifest(policy);

        return policy.guid();
    }

    @Override
    public IGUID addContext(String jsonContext) throws ContextException {

        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(jsonContext);
            ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);
            return addContext(contextBuilder);

        } catch (IOException e) {
            throw new ContextException("Unable to add context to SOS from json string");
        }
    }

    @Override
    public IGUID addContext(File file) throws ContextException {

        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(file);
            ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);
            return addContext(contextBuilder);

        } catch (IOException e) {
            throw new ContextException("Unable to add context to SOS from file");
        }
    }

    @Override
    public IGUID updateContext(Context previous, ContextBuilder contextBuilder) throws ContextException {

        if (contextBuilder.getContextBuilderType() == ContextBuilder.ContextBuilderType.TEMP) {

            try {
                Compound contents = contextBuilder.getContents();

                if (!previous.content().equals(contents.guid()) ||
                        !previous.domain().equals(contextBuilder.getDomain()) ||
                        !previous.codomain().equals(contextBuilder.getCodomain()) ||
                        previous.maxAge() != contextBuilder.getMaxage()) {

                    manifestsDataService.addManifest(contents);

                    Instant contextTimestamp = Instant.now();
                    Context context = new ContextManifest(contextTimestamp, previous.getName(),
                            contextBuilder.getDomain(), contextBuilder.getCodomain(),
                            previous.predicate(), previous.maxAge(), previous.policies(), null,
                            contents.guid(), previous.invariant(), previous.guid());
                    manifestsDataService.addManifest(context);

                    return context.guid();
                }

            } catch (ManifestPersistException e) {
                throw new ContextException(e);
            }
        }

        return previous.guid();
    }

    public Context getContext(IGUID contextGUID) throws ContextNotFoundException {

        try {
            return (Context) manifestsDataService.getManifest(contextGUID, NodeType.CMS);
        } catch (ManifestNotFoundException e) {
            throw new ContextNotFoundException(e);
        }
    }

    @Override
    public Context getContextTIP(IGUID invariant) throws TIPNotFoundException, ContextNotFoundException {

        // Should have only one tip per time
        Iterator<IGUID> tips = manifestsDataService.getTips(invariant).iterator();
        if (tips.hasNext()) {
            IGUID tip = tips.next();
            return getContext(tip);
        }

        throw new TIPNotFoundException();
    }

    @Override
    public Set<Context> searchContexts(String contextName) throws ContextNotFoundException {

        List<ManifestParam> params = new LinkedList<>();
        params.add(new ManifestParam(JSONConstants.KEY_CONTEXT_NAME, contextName));

        Set<IGUID> contextsFound = manifestsDataService.searchVersionableManifests(ManifestType.CONTEXT, params);

        Set<Context> retval = new LinkedHashSet<>();
        for(IGUID contextRef:contextsFound) {
            Context context = getContext(contextRef);
            retval.add(context);
        }

        return retval;
    }

    @Override
    public Set<IGUID> getContents(IGUID contextGUID) {

        Set<IGUID> retval = new LinkedHashSet<>();

        try {
            Context context = getContext(contextGUID);
            long maxAge = context.maxAge();
            Instant contextTimestamp = context.timestamp();
            long diff;

            Set<IGUID> invariants = new LinkedHashSet<>();
            // Iterate through previous versions of context until max-age constraint is still valid
            do {
                Compound compound = (Compound) manifestsDataService.getManifest(context.content(), NodeType.DDS);

                for(Content content:compound.getContents()) {

                    IGUID contentGUID = content.getGUID();
                    Manifest manifest = manifestsDataService.getManifest(contentGUID);
                    if (manifest.getType() == ManifestType.VERSION) {

                        Version version = (Version) manifest;
                        IGUID invariant = version.invariant();

                        // Avoid returning two versions of the same asset.
                        // This method should return only the latest entry in relation to the starting contextGUID.
                        if (!invariants.contains(invariant)) {
                            invariants.add(invariant);

                            retval.add(contentGUID);
                        }

                    }
                }

                Instant currentContextTimestamp = context.timestamp();
                diff = ChronoUnit.SECONDS.between(currentContextTimestamp, contextTimestamp); // This order of Instants gives a positive number on the diff

                if (!context.previous().isEmpty()) {
                    IGUID previous = context.previous().iterator().next();
                    context = getContext(previous);
                } else {
                    context = null;
                }
            } while(context != null && diff <= maxAge);


        } catch (ContextNotFoundException | ManifestNotFoundException e) {

            return new LinkedHashSet<>();
        }

        return retval;
    }

    @Override
    public ContextVersionInfo getContextContentInfo(IGUID contextInvariant, IGUID version) {

        return contextsContentsDirectory.getEntry(contextInvariant, version);
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile contextsContentsFile = localStorage.createFile(cacheDir, CMS_INDEX_FILE);
            Persistence.Persist(contextsContentsDirectory, contextsContentsFile);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the CMS index");
        }
    }

    @Override
    public void shutdown() {

        if (service != null) {
            service.shutdown();
        }

        // RESET IN-MEMORY DATA STRUCTURES
        cachedComputationalUnits = new LinkedHashMap<>();
        predicateThreadSessionStatistics = new LinkedList<>();
        applyPolicyThreadSessionStatistics = new LinkedList<>();
        checkPolicyThreadSessionStatistics = new LinkedList<>();
    }

    ////////////////////////////////////////////////////////////
    ////////////////////// STATS ///////////////////////////////
    ////////////////////////////////////////////////////////////

    @Override
    public Queue<Pair<Long, Long>> getPredicateThreadSessionStatistics() {

        return predicateThreadSessionStatistics;
    }

    @Override
    public Queue<Pair<Long, Long>> getApplyPolicyThreadSessionStatistics() {

        return applyPolicyThreadSessionStatistics;
    }

    @Override
    public Queue<Pair<Long, Long>> getCheckPolicyThreadSessionStatistics() {

        return checkPolicyThreadSessionStatistics;
    }

    ////////////////////////////////////////////////////////////
    ////////////////////// BYPASS THREADS //////////////////////
    ////////////////////////////////////////////////////////////

    @Override
    public void runContextPredicateNow(IGUID guid) throws ContextNotFoundException {

        Context context = getContext(guid);
        runContextPredicateNow(context);
    }

    @Override
    public void runContextPolicyNow(IGUID guid) throws ContextNotFoundException {

        Context context = getContext(guid);
        runContextPoliciesNow(context);
    }

    @Override
    public void runContextPolicyCheckNow(IGUID guid) throws ContextNotFoundException {

        Context context = getContext(guid);
        runContextPoliciesCheckNow(context);
    }

    ////////////////////////////////////////////////////////////
    ////////////////////// PREDICATE ///////////////////////////
    ////////////////////////////////////////////////////////////

    private void runContextPredicateNow(Context context) {

        service.schedule(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running ACTIVELY predicate for context " + context.getName());

            long start = System.currentTimeMillis();

            try {
                runPredicate(context);
            } catch (ContextException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to run predicates for context " + context.getUniqueName() + " properly");
            }

            long end = System.currentTimeMillis();
            predicateThreadSessionStatistics.add(new Pair<>(start, end));

        }, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Run PERIODIC predicates.
     *
     * Iterate over all active contexts.
     * For each context:
     * Run all known tips of assets against the predicate of the context.
     *
     */
    private void runPredicatesPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings predicateThreadSettings = SOSLocalNode.settings.getServices().getCms().getPredicateThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running predicates - this is a periodic background thread");

            long start = System.currentTimeMillis();
            runPredicates();
            long end = System.currentTimeMillis();
            predicateThreadSessionStatistics.add(new Pair<>(start, end));

        }, predicateThreadSettings.getInitialDelay(), predicateThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    /**
     * This method is public so that it is accessible to the experiments too
     * @return number of assets processed by all contexts
     */
    @Override
    public int runPredicates() {

        int counter = 0;

        for (Context context : getContexts()) {
            SOS_LOG.log(LEVEL.INFO, "Running predicate for context " + context.getUniqueName());
            try {

                pred_time_prep = 0;
                pred_time_to_check_if_predicate_has_to_be_run = 0;
                pred_time_to_run_predicate_on_current_dataset = 0;
                pred_time_to_update_context = 0;

                counter += runPredicate(context);

                InstrumentFactory.instance().measure(StatsTYPE.predicate, StatsTYPE.predicate_prep, context.getName(), pred_time_prep);
                InstrumentFactory.instance().measure(StatsTYPE.predicate, StatsTYPE.predicate_check, context.getName(), pred_time_to_check_if_predicate_has_to_be_run);
                InstrumentFactory.instance().measure(StatsTYPE.predicate, StatsTYPE.predicate_dataset, context.getName(), pred_time_to_run_predicate_on_current_dataset);
                InstrumentFactory.instance().measure(StatsTYPE.predicate, StatsTYPE.predicate_update_context, context.getName(), pred_time_to_update_context);

            } catch (ContextException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to run predicates for context " + context.getUniqueName() + " properly");
            }
            SOS_LOG.log(LEVEL.INFO, "Finished to run predicate for context " + context.getUniqueName());
        }

        return counter;
    }

    private int runPredicate(Context context) throws ContextException {

        long start = System.nanoTime();
        int counter = 0;

        Set<Content> contents = new LinkedHashSet<>();
        Set<Pair<IGUID, ContextVersionInfo>> tempResults = new LinkedHashSet<>();
        Set<IGUID> assetInvariants = manifestsDataService.getManifests(ManifestType.VERSION);

        pred_time_prep = System.nanoTime() - start; // Time before running the context on each asset

        for (IGUID assetInvariant:assetInvariants) {

            try {
                IGUID head = manifestsDataService.getHead(assetInvariant);
                boolean predicateResult = runPredicate(context, assetInvariant, head);

                if (predicateResult) {
                    Content content = new ContentImpl(head);
                    contents.add(content);

                    ContextVersionInfo contentInfo = new ContextVersionInfo();
                    contentInfo.predicateResult = true;
                    contentInfo.timestamp = Instant.now();
                    tempResults.add(new Pair<>(head, contentInfo));
                }

                counter++;
            } catch (HEADNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find head for invariant: " + assetInvariant.toMultiHash());
            }

        }

        // Updating context with new contents
        start = System.nanoTime();

        try {
            // The context will contain only the new processed assets. To get all assets, we need to go back through the previous versions.
            Compound contextContents = new CompoundManifest(CompoundType.COLLECTION, contents, null);
            ContextBuilder contextBuilder = new ContextBuilder(context.guid(), contextContents, context.domain(), context.codomain(), context.maxAge());
            updateContext(context, contextBuilder);

            IGUID contextInvariant = context.invariant();
            for(Pair<IGUID, ContextVersionInfo> info:tempResults) {
                contextsContentsDirectory.addOrUpdateEntry(contextInvariant, info.X(), info.Y());
            }

        } catch (ManifestNotMadeException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to update context version properly");
            throw new ContextException("Unable to update context version properly");
        }

        pred_time_to_update_context = System.nanoTime() - start; // Time after predicate is run and used to process the results

        return counter;
    }

    /**
     * Run the predicate of the given context against the specified version
     *
     * - Check if the predicate has already apply and if the maxAge constraint is still valid.
     * - If the answer to the above is NO/False:
     *  - Run the predicate of the context against the given version
     *  - Update the contextsContents
     *
     * @param context for which to run the predicate
     * @param assetInvariant of the asset processed
     * @param versionGUID to evaluate
     * @return true if the predicate was run and it was true. This is not indicative of the result of the predicate.
     */
    private boolean runPredicate(Context context, IGUID assetInvariant, IGUID versionGUID) {

        long start = System.nanoTime();
        boolean predicateResult = false;

        IGUID contextInvariant = context.invariant();
        // The `contextsContentsDirectory` data structure is needed to run this process faster and avoid unnecessary re-runs.
        // This data structure keeps track of entries and their results (negative results too).
        boolean alreadyRun = contextsContentsDirectory.entryExists(contextInvariant, versionGUID);
        boolean maxAgeExpired = false;

        if (alreadyRun) {
            maxAgeExpired = predicateHasExpired(context, versionGUID);
        }

        long duration = System.nanoTime() - start;
        pred_time_to_check_if_predicate_has_to_be_run += duration;

        if (!alreadyRun || maxAgeExpired) {

            Predicate predicate = getPredicate(context);

            start = System.nanoTime();
            predicateResult = predicate.test(versionGUID);
            duration = System.nanoTime() - start;
            pred_time_to_run_predicate_on_current_dataset += duration;

            evictEntriesForInvariant(contextInvariant, assetInvariant, versionGUID);
        }

        return predicateResult;
    }

    /**
     * Check if the predicate of a context is still valid a given version or not
     *
     * @param context for which the predicate should be checked
     * @param versionGUID to evaluate
     * @return true if the predicate is still valid
     */
    private boolean predicateHasExpired(Context context, IGUID versionGUID) {

        ContextVersionInfo content =  contextsContentsDirectory.getEntry(context.invariant(), versionGUID);

        long max_age = context.maxAge();
        Instant contentLastRun = content.timestamp;
        Instant now = Instant.now();
        long diff = ChronoUnit.SECONDS.between(contentLastRun, now);

        return diff > max_age;
    }

    /**
     * Get Predicate object for given context.
     * If the Predicate object is cached, then get that one instead of loading it from disk.
     *
     * @param context for which to get the predicate
     * @return predicate
     */
    private Predicate getPredicate(Context context) {

        IGUID predicateRef = context.predicate();

        if (cachedComputationalUnits.containsKey(predicateRef)) {
            return (Predicate) cachedComputationalUnits.get(predicateRef);
        }

        try {

            Predicate predicate = (Predicate) manifestsDataService.getManifest(predicateRef, NodeType.CMS);
            cachedComputationalUnits.put(predicateRef, predicate);
            return predicate;

        } catch (ManifestNotFoundException e) {

            JsonNode emptyJsonNode = JSONHelper.JsonObjMapper().createObjectNode();
            return new ReferencePredicate(emptyJsonNode);
        }
    }

    /**
     * Evicting previous results for this asset.
     *
     */
    private void evictEntriesForInvariant(IGUID contextInvariant, IGUID assetInvariant, IGUID versionGUID) {
        Set<IGUID> versions = manifestsDataService.getVersions(assetInvariant);
        for(IGUID version:versions) {

            if (!version.equals(versionGUID)) {
                contextsContentsDirectory.evict(contextInvariant, version);
            }
        }
    }

    ////////////////////////////////////////////////////////////
    ////////////////////// POLICIES ////////////////////////////
    ////////////////////////////////////////////////////////////

    private void runContextPoliciesNow(Context context) {

        service.schedule(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running ACTIVELY policies for context " + context.getName());

            long start = System.currentTimeMillis();
            runPolicies(context);
            long end = System.currentTimeMillis();
            applyPolicyThreadSessionStatistics.add(new Pair<>(start, end));

        }, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Run PERIODIC policies.
     *
     * Iterate over contexts
     * For each context:
     * - Get all versions for the context such that:
     *  (1) predicate result was true and
     *  (2) no policies has been apply yet
     *
     */
    private void runPoliciesPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings policiesThreadSettings = SOSLocalNode.settings.getServices().getCms().getPoliciesThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running policies - this is a periodic background thread");

            long start = System.currentTimeMillis();
            runPolicies();
            long end = System.currentTimeMillis();
            applyPolicyThreadSessionStatistics.add(new Pair<>(start, end));

        }, policiesThreadSettings.getInitialDelay(), policiesThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    @Override
    public void runPolicies() {

        Set<Context> contexts = getContexts();
        for (Context context : contexts) {
            SOS_LOG.log(LEVEL.INFO, "Running policies for context " + context.getUniqueName());

            policy_time_to_run_apply_on_current_dataset = 0;
            runPolicies(context);
            InstrumentFactory.instance().measure(StatsTYPE.policies, StatsTYPE.policy_apply_dataset, context.getName(), policy_time_to_run_apply_on_current_dataset);

            SOS_LOG.log(LEVEL.INFO, "Running policies for context " + context.getUniqueName());
        }

    }

    private void runPolicies(Context context) {

        IGUID contextInvariant = context.invariant();
        Map<IGUID, ContextVersionInfo> contentsToProcess = contextsContentsDirectory.getContentsThatPassedPredicateTestRows(contextInvariant, false);
        contentsToProcess.forEach((guid, row) -> {

            if (row.predicateResult && !row.policySatisfied) {
                runPolicies(context, guid);
            }

        });
    }

    /**
     * Run the policies of a given context for the specified entity
     *
     * @param context for which policies have to run
     * @param guid of the entity
     */
    private void runPolicies(Context context, IGUID guid) {

        try {
            Manifest manifest = manifestsDataService.getManifest(guid, NodeType.DDS);

            ContextVersionInfo content = new ContextVersionInfo();
            ContextVersionInfo prev = contextsContentsDirectory.getEntry(context.invariant(), guid);

            // NOTE - this is a naive way to update only the policy result
            content.predicateResult = prev.predicateResult;
            content.timestamp = prev.timestamp;

            Set<Policy> policies = getPolicies(context);

            long start = System.nanoTime();
            for (Policy policy:policies) {
                policy.apply(context.codomain(), commonUtilities, manifest);
            }
            long duration = System.nanoTime() - start;
            policy_time_to_run_apply_on_current_dataset += duration;

            contextsContentsDirectory.addOrUpdateEntry(context.invariant(), guid, content);

        } catch (ManifestNotFoundException | PolicyException e) {

            SOS_LOG.log(LEVEL.ERROR, "Unable to run policies for context " + context.guid().toMultiHash() +
                    " and entry " + guid.toMultiHash());
        }
    }

    private Set<Policy> getPolicies(Context context) {

        Set<Policy> retval = new LinkedHashSet<>();

        for(IGUID policyRef:context.policies()) {

            if (cachedComputationalUnits.containsKey(policyRef)) {
                Policy policy = (Policy) cachedComputationalUnits.get(policyRef);
                retval.add(policy);

                continue;
            }

            try {
                Policy policy = (Policy) manifestsDataService.getManifest(policyRef, NodeType.CMS);
                cachedComputationalUnits.put(policyRef, policy);
                retval.add(policy);

            } catch (ManifestNotFoundException e) {

                return new LinkedHashSet<>();
            }
        }


        return retval;
    }

    //////////////////////////////////////////////////////////////////
    ////////////////////// CHECK POLICIES ////////////////////////////
    //////////////////////////////////////////////////////////////////

    private void runContextPoliciesCheckNow(Context context) {

        service.schedule(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running ACTIVELY policies check for context " + context.getName());

            long start = System.currentTimeMillis();
            runCheckPolicies(context);
            long end = System.currentTimeMillis();
            checkPolicyThreadSessionStatistics.add(new Pair<>(start, end));

        }, 0, TimeUnit.MILLISECONDS);
    }

    private void checkPoliciesPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings checkPoliciesThreadSettings = SOSLocalNode.settings.getServices().getCms().getCheckPoliciesThread();

        service.scheduleWithFixedDelay(() -> {

            long start = System.currentTimeMillis();
            runCheckPolicies();
            long end = System.currentTimeMillis();
            checkPolicyThreadSessionStatistics.add(new Pair<>(start, end));

        }, checkPoliciesThreadSettings.getInitialDelay(), checkPoliciesThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    @Override
    public void runCheckPolicies() {

        for (Context context : getContexts()) {

            // Set the dataset stat to zero. This field is then increased by runCheckPolicies
            policy_time_to_run_check_on_current_dataset = 0;

            runCheckPolicies(context);

            InstrumentFactory.instance().measure(StatsTYPE.checkPolicies, StatsTYPE.policy_check_dataset, context.getName(), policy_time_to_run_check_on_current_dataset);
        }
    }

    private void runCheckPolicies(Context context) {

        long start = System.nanoTime();

        IGUID contextInvariant = context.invariant();
        Map<IGUID, ContextVersionInfo> contentsToProcess = contextsContentsDirectory.getContentsThatPassedPredicateTestRows(contextInvariant, false);
        contentsToProcess.forEach((guid, row) -> {
            if (row.predicateResult) {
                runCheckPolicies(context, guid);
            }
        });

        long duration = System.nanoTime() - start;
        InstrumentFactory.instance().measure(StatsTYPE.predicate, StatsTYPE.checkPolicies, context.getName(), duration);
    }

    private void runCheckPolicies(Context context, IGUID guid) {

        try {
            ContextVersionInfo content = new ContextVersionInfo();
            ContextVersionInfo prev = contextsContentsDirectory.getEntry(context.invariant(), guid);

            // NOTE - this is a naive way to update only the policy result
            content.predicateResult = prev.predicateResult;
            content.timestamp = prev.timestamp;

            Set<Policy> policies = getPolicies(context);
            boolean allPoliciesAreSatisfied = true;
            Manifest manifest = manifestsDataService.getManifest(guid, NodeType.DDS);

            long start = System.nanoTime();
            for (Policy policy:policies) {
                allPoliciesAreSatisfied = allPoliciesAreSatisfied && policy.satisfied(context.codomain(), commonUtilities, manifest);;
            }
            long duration = System.nanoTime() - start;
            policy_time_to_run_check_on_current_dataset += duration;

            content.policySatisfied = allPoliciesAreSatisfied;
            contextsContentsDirectory.addOrUpdateEntry(context.invariant(), guid, content);

        } catch (ManifestNotFoundException | PolicyException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////
    ////////////////////// SPAWN CONTEXT ////////////////////////////
    /////////////////////////////////////////////////////////////////

    /**
     * Periodically spawn/replicate contexts to other nodes
     *
     * Some notes:
     * 1. Iterate over all known local contexts
     * 2. filter by contexts that should be run over multiple nodes
     * 3. make a call to the ContextDefinitionReplication TASK
     * ADDITIONAL - 4. if the context cannot be spawned (maybe other node does not want us to run the context there! or it is a storage node),
     * then mark that and use #getDataPeriodic to get data to be processed from that node
     */
    private void spawnContextsOverDomainPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings spawnThreadSettings = SOSLocalNode.settings.getServices().getCms().getSpawnThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Spawn contexts to other nodes of the specified domain - this is a periodic background thread");

            for (Context context : getContexts()) {

                NodesCollection nodesCollection = context.domain();
                if (nodesCollection.type() == NodesCollectionType.SPECIFIED) {

                    try {
                        spawnContext(context);
                    } catch (ManifestPersistException e) {
                        SOS_LOG.log(LEVEL.ERROR, "Unable to spawn context " + context.guid().toMultiHash());
                    }
                }
            }

        }, spawnThreadSettings.getInitialDelay(), spawnThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    /**
     * Replicate the context to the nodes within the domain.
     *
     * @param context to be spawned
     */
    private void spawnContext(Context context) throws ManifestPersistException {

        // The context is spawned to all the nodes in the domain. So the replication factor == node-cardinality(domain)
        NodesCollection domain = context.domain();
        int replication = domain.nodesRefs().size();
        manifestsDataService.addManifest(context, domain, replication);
    }

}

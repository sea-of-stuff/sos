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
import uk.ac.standrews.cs.sos.impl.context.ContextBuilder;
import uk.ac.standrews.cs.sos.impl.context.ContextManifest;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextVersionInfo;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContentsDirectoryFactory;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContentsDirectoryType;
import uk.ac.standrews.cs.sos.impl.context.examples.ReferencePolicy;
import uk.ac.standrews.cs.sos.impl.context.examples.ReferencePredicate;
import uk.ac.standrews.cs.sos.impl.datamodel.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestParam;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.interfaces.context.ContextsContentsDirectory;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;

/**
 * The SOSContextService managed the contexts for this node.
 *
 * NOTE - IDEA should have a lock on content (e.g. this content is being managed by this policy for the moment, thus halt)
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSContextService implements ContextService {

    // NODE SERVICES AND UTILS
    private LocalStorage localStorage;
    private ManifestsDataService manifestsDataService;

    private PolicyActions policyActions;

    // DATA STRUCTURES
    private ContextsContentsDirectory contextsContentsDirectory;

    // This executor service will be used to schedule any background tasks
    private static final int CMS_SCHEDULER_PS = 5;
    private ScheduledExecutorService service;
    private Queue<Pair<Long, Long>> predicateThreadSessionStatistics;
    private Queue<Pair<Long, Long>> applyPolicyThreadSessionStatistics;
    private Queue<Pair<Long, Long>> checkPolicyThreadSessionStatistics;

    /**
     * Build a CMS instance.
     * The DDS is passed as parameter and it is needed to access the manifests to be processed.
     *
     * @param localStorage used to persist the internal data structures
     * @param manifestsDataService used to discover the data to process and act upon it
     * @param nodeDiscoveryService used to operate on the SOS and its nodes
     * @param usersRolesService uses to perform USER/ROLE operations in the SOS
     * @param storageService used to access the SOS storage
     */
    public SOSContextService(LocalStorage localStorage, ManifestsDataService manifestsDataService, NodeDiscoveryService nodeDiscoveryService, UsersRolesService usersRolesService, StorageService storageService) throws ServiceException {

        try {

            this.localStorage = localStorage;
            this.manifestsDataService = manifestsDataService;
            policyActions = new PolicyActions(nodeDiscoveryService, manifestsDataService, usersRolesService, storageService);

            contextsContentsDirectory = new ContextsContentsDirectoryFactory().makeContextsContentsDirectory(ContextsContentsDirectoryType.IN_MEMORY, localStorage);
            loadContexts();

            predicateThreadSessionStatistics = new LinkedList<>();
            applyPolicyThreadSessionStatistics = new LinkedList<>();
            checkPolicyThreadSessionStatistics = new LinkedList<>();

            // Run background CRON Jobs if and only if this is set in the node settings file
            if (SOSLocalNode.settings.getServices().getCms().isAutomatic()) {

                service = new ScheduledThreadPoolExecutor(CMS_SCHEDULER_PS);
                runPredicatesPeriodic();
                runPoliciesPeriodic();
                checkPoliciesPeriodic();
                getDataPeriodic();
                spawnContextsPeriodic();
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

        Set<IGUID> contextInvariants = manifestsDataService.getInvariants(ManifestType.CONTEXT);
        for(IGUID contextInvariant:contextInvariants) {
            try {
                Iterator<IGUID> tips = manifestsDataService.getTips(contextInvariant).iterator();
                if (tips.hasNext()) {

                    IGUID contextTip = tips.next();
                    Context context = getContext(contextTip);
                    contexts.add(context);
                }

            } catch (TIPNotFoundException | ContextNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get context tip from invariant ref: " + contextInvariant.toMultiHash());
                /* SKIP */
            }
        }

        // Shuffling contexts to avoid executing them always in the same order
        List<Context> temp = new ArrayList<>(contexts);
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
    public void updateContext(Context previous, ContextBuilder contextBuilder) throws ContextException {

        if (contextBuilder.getContextBuilderType() == ContextBuilder.ContextBuilderType.TEMP) {

            try {
                Compound contents = contextBuilder.getContents();

                if (!previous.content().equals(contents.guid()) ||
                    !previous.domain().equals(contextBuilder.getDomain()) ||
                    !previous.codomain().equals(contextBuilder.getCodomain()) ||
                    previous.maxAge() != contextBuilder.getMaxage()) {

                    manifestsDataService.addManifest(contents);

                    Context context = new ContextManifest(previous.getName(), contextBuilder.getDomain(), contextBuilder.getCodomain(),
                            previous.predicate(), previous.maxAge(), previous.policies(), null, contents.guid(), previous.invariant(), previous.guid());
                    manifestsDataService.addManifest(context);
                }

            } catch (ManifestPersistException e) {
                throw new ContextException(e);
            }
        }

    }

    public Context getContext(IGUID contextGUID) throws ContextNotFoundException {

        try {
            return (Context) manifestsDataService.getManifest(contextGUID);
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

        try {
            Context context = getContext(contextGUID);

            Compound compound = (Compound) manifestsDataService.getManifest(context.content());
            return compound.getContents().stream()
                    .map(Content::getGUID)
                    .collect(Collectors.toSet());

        } catch (ContextNotFoundException | ManifestNotFoundException e) {

            return new LinkedHashSet<>();
        }
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
    }

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

    @Override
    public ContextVersionInfo getContextContentInfo(IGUID context, IGUID version) {

        return contextsContentsDirectory.getEntry(context, version);
    }

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

    ////////////////////
    // PERIODIC TASKS //
    ////////////////////

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

    public int runPredicates() {

        int counter = 0;

        for (Context context : getContexts()) {
            try {
                counter += runPredicate(context);
            } catch (ContextException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to run predicates for context " + context.getUniqueName() + " properly");
            }
        }

        return counter;
    }

    private int runPredicate(Context context) throws ContextException {

        int counter = 0;
        long start = System.nanoTime();


        Set<Content> currentContents;
        try {
            Compound compound = (Compound) manifestsDataService.getManifest(context.content());
            currentContents = compound.getContents();
        } catch (ManifestNotFoundException e) {
            throw new ContextException("Unable to get the context's current contents");
        }

        Set<Content> contents = new LinkedHashSet<>();
        Set<IGUID> assetInvariants = manifestsDataService.getInvariants(ManifestType.VERSION);
        for (IGUID assetInvariant:assetInvariants) {

            try {
                IGUID head = manifestsDataService.getHead(assetInvariant);
                boolean predicateResult = runPredicate(context, currentContents, assetInvariant, head);
                if (predicateResult) {
                    Content content = new ContentImpl(head);
                    contents.add(content);
                }

                counter++;
            } catch (HEADNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find head for invariant: " + assetInvariant.toMultiHash());
            }

        }

        try {
            Compound contextContents = new CompoundManifest(CompoundType.COLLECTION, contents, null);
            ContextBuilder contextBuilder = new ContextBuilder(context.guid(), contextContents, context.domain(), context.codomain(), context.maxAge());
            updateContext(context, contextBuilder);

        } catch (ManifestNotMadeException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to update context version properly");
            throw new ContextException("Unable to update context version properly");
        }

        long duration = System.nanoTime() - start;
        InstrumentFactory.instance().measure(StatsTYPE.predicate, context.getName(), duration);

        return counter;
    }

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

        for (Context context : getContexts()) {
            runPolicies(context);
        }

    }

    private void runPolicies(Context context) {

        long start = System.nanoTime();

        Map<IGUID, ContextVersionInfo> contentsToProcess =  contextsContentsDirectory.getContentsThatPassedPredicateTestRows(context.guid(), false);
        contentsToProcess.forEach((guid, row) -> {

            if (row.predicateResult && !row.policySatisfied) {
                runPolicies(context, guid);
            }

        });

        long duration = System.nanoTime() - start;
        InstrumentFactory.instance().measure(StatsTYPE.policies, context.getName(), duration);
    }

    private void runContextPoliciesCheckNow(Context context) {

        service.schedule(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running ACTIVELY policies check for context " + context.getName());

            long start = System.currentTimeMillis();
            checkPolicies(context);
            long end = System.currentTimeMillis();
            checkPolicyThreadSessionStatistics.add(new Pair<>(start, end));

        }, 0, TimeUnit.MILLISECONDS);
    }

    private void checkPoliciesPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings checkPoliciesThreadSettings = SOSLocalNode.settings.getServices().getCms().getCheckPoliciesThread();

        service.scheduleWithFixedDelay(() -> {

            long start = System.currentTimeMillis();
            checkPolicies();
            long end = System.currentTimeMillis();
            checkPolicyThreadSessionStatistics.add(new Pair<>(start, end));

        }, checkPoliciesThreadSettings.getInitialDelay(), checkPoliciesThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    private void checkPolicies() {

        for (Context context : getContexts()) {
            checkPolicies(context);
        }
    }

    private void checkPolicies(Context context) {

        long start = System.nanoTime();

        Map<IGUID, ContextVersionInfo> contentsToProcess = contextsContentsDirectory.getContentsThatPassedPredicateTestRows(context.guid(), false);
        contentsToProcess.forEach((guid, row) -> {
            if (row.predicateResult) {
                checkPolicies(context, guid);
            }
        });

        long duration = System.nanoTime() - start;
        InstrumentFactory.instance().measure(StatsTYPE.checkPolicies, context.getName(), duration);
    }

    /**
     * Periodically get data (or references?) from other nodes
     * as specified by the sources of a context
     */
    private void getDataPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings getDataPeriodicThreadSettings = SOSLocalNode.settings.getServices().getCms().getGetdataThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.WARN, "N/A yet - Get data from other nodes - this is a periodic background thread");

            for (Context context : getContexts()) {

                // TODO - run this only for those contexts/nodes that have been marked (data-periodic) by the spawnContextsPeriodic logic (see comments in there)
                NodesCollection domain = context.domain();


                // TODO - before downloading data, we should check the following:
                // Instead of downloading the data straight away, better to get a list of the data first and then request only what we need
                //
                // 1. do we have the data already?
                // 2. does another node have this data and this context? if so, do we have any results from there?
                // 3. if the answer to all the above if no, then download data from some known location
            }

        }, getDataPeriodicThreadSettings.getInitialDelay(), getDataPeriodicThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    /**
     * Periodically spawn/replicate contexts to other nodes
     */
    private void spawnContextsPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings spawnThreadSettings = SOSLocalNode.settings.getServices().getCms().getSpawnThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.WARN, "N/A yet - Spawn contexts to other nodes - this is a periodic background thread");

            // TODO
            // 1. Iterate over all known local contexts
            // 2. filter by contexts that should be run over multiple nodes
            // 3. make a call to the ContextDefinitionReplication TASK
            // 4. if the context cannot be spawned (maybe other node does not want us to run the context there! or it is a storage node),
            // then mark that and use #getDataPeriodic to get data to be processed from that node

        }, spawnThreadSettings.getInitialDelay(), spawnThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    /////////////////////
    // PRIVATE METHODS //
    /////////////////////

    /**
     * Run the predicate of the given context against the specified version
     *
     * - Check if the predicate has already apply and if the maxAge constraint is still valid.
     * - If the answer to the above is NO/False:
     *  - Run the predicate of the context against the given version
     *  - Update the contextsContents
     *
     * @param context for which to run the predicate
     * @param assetInvariant of the version
     * @param versionGUID to evaluate
     */
    private boolean runPredicate(Context context, Set<Content> currentContents, IGUID assetInvariant, IGUID versionGUID) {

        boolean predicateResult = false;

        IGUID contextGUID = context.guid();
        // The `contextsContentsDirectory` data structure is needed to run this process faster and avoid unnecessary re-runs
        // In fact, this data structure keeps track of entries that have negative results too
        boolean alreadyRun = contextsContentsDirectory.entryExists(contextGUID, versionGUID);
        boolean maxAgeExpired = false;

        if (alreadyRun) {
            predicateResult = currentContents.stream().anyMatch(c -> c.getGUID().equals(versionGUID));
            maxAgeExpired = predicateHasExpired(context, versionGUID);
        }

        if (!alreadyRun || maxAgeExpired) {

            Predicate predicate = getPredicate(context);
            predicateResult = predicate.test(versionGUID);

            ContextVersionInfo content = new ContextVersionInfo();
            content.predicateResult = predicateResult;
            content.timestamp = System.currentTimeMillis();

            for(IGUID version: manifestsDataService.getVersions(assetInvariant)) {

                if (!version.equals(versionGUID)) {
                    contextsContentsDirectory.evict(contextGUID, version);
                }
            }

            contextsContentsDirectory.addEntry(contextGUID, versionGUID, content);
        }

        return predicateResult;
    }

    /**
     * Run the policies of a given context for the specified entity
     *
     * @param context for which policies have to run
     * @param guid of the entity
     */
    private void runPolicies(Context context, IGUID guid) {

        try {
            ContextVersionInfo content = new ContextVersionInfo();
            ContextVersionInfo prev = contextsContentsDirectory.getEntry(context.guid(), guid);

            // NOTE - this is a naive way to update only the policy result
            content.predicateResult = prev.predicateResult;
            content.timestamp = prev.timestamp;

            Set<Policy> policies = getPolicies(context);
            boolean allPoliciesAreSatisfied = true;
            for (Policy policy:policies) {

                Manifest manifest = manifestsDataService.getManifest(guid);
                policy.apply(context.codomain(), policyActions, manifest);
                allPoliciesAreSatisfied = allPoliciesAreSatisfied && policy.satisfied(context.codomain(), policyActions, manifest);
            }

            content.policySatisfied = allPoliciesAreSatisfied;
            contextsContentsDirectory.addEntry(context.guid(), guid, content);

        } catch (ManifestNotFoundException | PolicyException e) {
            e.printStackTrace();
        }
    }

    private void checkPolicies(Context context, IGUID guid) {

        try {
            ContextVersionInfo content = new ContextVersionInfo();
            ContextVersionInfo prev =  contextsContentsDirectory.getEntry(context.guid(), guid);

            // NOTE - this is a naive way to update only the policy result
            content.predicateResult = prev.predicateResult;
            content.timestamp = prev.timestamp;

            Set<Policy> policies = getPolicies(context);
            boolean allPoliciesAreSatisfied = true;
            for (Policy policy:policies) {

                Manifest manifest = manifestsDataService.getManifest(guid);
                allPoliciesAreSatisfied = allPoliciesAreSatisfied && policy.satisfied(context.codomain(), policyActions, manifest);;
            }

            content.policySatisfied = allPoliciesAreSatisfied;
            contextsContentsDirectory.addEntry(context.guid(), guid, content);

        } catch (ManifestNotFoundException | PolicyException e) {
            e.printStackTrace();
        }
    }


    /**
     * Check if the predicate of a context is still valid a given version or not
     *
     * @param context for which the predicate should be checked
     * @param versionGUID to evaluate
     * @return true if the predicate is still valid
     */
    private boolean predicateHasExpired(Context context, IGUID versionGUID) {

        ContextVersionInfo content =  contextsContentsDirectory.getEntry(context.guid(), versionGUID);

        long max_age = context.maxAge();
        long contentLastRun = content.timestamp;
        long now = System.currentTimeMillis();

        return (now - contentLastRun) > max_age;
    }

    private void loadContexts() throws ServiceException {

        // TODO - load predicates/policies for contexts
    }

    private Predicate getPredicate(Context context) {

        IGUID predicateRef = context.predicate();

        try {
            return (Predicate) manifestsDataService.getManifest(predicateRef);

        } catch (ManifestNotFoundException e) {

            JsonNode emptyJsonNode = JSONHelper.JsonObjMapper().createObjectNode();
            return new ReferencePredicate(emptyJsonNode);
        }
    }

    private Set<Policy> getPolicies(Context context) {

        Set<Policy> retval = new LinkedHashSet<>();

        for(IGUID policyRef:context.policies()) {

            try {
                Policy policy = (Policy) manifestsDataService.getManifest(policyRef);
                retval.add(policy);

            } catch (ManifestNotFoundException e) {

                retval.clear();

                JsonNode emptyJsonNode = JSONHelper.JsonObjMapper().createObjectNode();
                Policy referencePolicy = new ReferencePolicy(emptyJsonNode);
                retval.add(referencePolicy);
                break;
            }
        }


        return retval;
    }

}

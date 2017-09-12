package uk.ac.standrews.cs.sos.impl.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.text.WordUtils;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.directory.*;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.interfaces.context.ContextsContentsDirectory;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.services.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;
import static uk.ac.standrews.cs.sos.impl.context.utils.ContextClassBuilder.*;

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
    private DataDiscoveryService dataDiscoveryService;

    private PolicyActions policyActions;

    // DATA STRUCTURES
    private LocalContextsDirectory localContextsDirectory; // TODO - need to be able to track active/inactive contexts
    // The inMemoryCache keeps the context objects for this node in memory.
    private CacheContextsDirectory inMemoryCache;
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
     * @param dataDiscoveryService used to discover the data to process and act upon it
     * @param nodeDiscoveryService used to operate on the SOS and its nodes
     * @param usersRolesService uses to perform USER/ROLE operations in the SOS
     * @param storage used to access the SOS storage
     */
    public SOSContextService(LocalStorage localStorage, DataDiscoveryService dataDiscoveryService, NodeDiscoveryService nodeDiscoveryService, UsersRolesService usersRolesService, Storage storage) throws ServiceException {

        this.localStorage = localStorage;
        this.dataDiscoveryService = dataDiscoveryService;
        policyActions = new PolicyActions(nodeDiscoveryService, dataDiscoveryService, usersRolesService, storage);

        localContextsDirectory = new LocalContextsDirectory(localStorage, policyActions);
        inMemoryCache = new CacheContextsDirectory();

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
    }

    @Override
    public Set<Context> getContexts() {

        return inMemoryCache.getContexts()
                .stream()
                .map(c -> {
                    try {
                        return getContext(c);
                    } catch (ContextNotFoundException e) {
                        return null;
                    }
                }).collect(Collectors.toSet());
    }

    @Override
    public IGUID addContext(Context context) throws Exception {

        localContextsDirectory.addContext(context);
        inMemoryCache.addContext(context);

        if (SOSLocalNode.settings.getServices().getCms().isPredicateOnNewContext()) {
            runContextPredicateNow(context);
        }

        return context.guid();
    }

    @Override
    public IGUID addContext(String jsonContext) throws Exception {

        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(jsonContext);
        String contextName = WordUtils.capitalize(jsonNode.get(CONTEXT_JSON_NAME).textValue());
        NodesCollection domain = makeNodesCollection(jsonNode, CONTEXT_JSON_DOMAIN);
        NodesCollection codomain = makeNodesCollection(jsonNode, CONTEXT_JSON_CODOMAIN);

        ContextLoader.LoadContext(jsonNode);

        Context context;
        if (jsonNode.has("guid")) {
            IGUID contextGUID = GUIDFactory.recreateGUID(jsonNode.get("guid").textValue());
            context = ContextLoader.Instance(contextName, jsonNode, policyActions, contextGUID, contextName, domain, codomain);
        } else {
            context = ContextLoader.Instance(contextName, jsonNode, policyActions, contextName, domain, codomain);
        }

        return addContext(context);
    }

    @Override
    public IGUID addContext(File file) throws Exception {

        JsonNode node = JSONHelper.JsonObjMapper().readTree(file);
        return addContext(node.toString());
    }

    public Context getContext(IGUID contextGUID) throws ContextNotFoundException {

        try {
            return inMemoryCache.getContext(contextGUID);

        } catch (ContextNotFoundException e) {

            return localContextsDirectory.getContext(contextGUID);
        }
    }

    @Override
    public Set<Context> searchContexts(String contextName) throws ContextNotFoundException {

        return inMemoryCache.getContexts(contextName);
    }

    @Override
    public Set<IGUID> getContents(IGUID context) {

        return contextsContentsDirectory.getVersionsThatPassedPredicateTest(context, false);
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
            runPredicate(context);
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
            counter += runPredicate(context);
        }

        return counter;
    }

    private int runPredicate(Context context) {

        int counter = 0;
        long start = System.nanoTime();

        Set<IGUID> assets = dataDiscoveryService.getAllAssets();
        for (IGUID assetInvariant : assets) {

            try {
                IGUID head = dataDiscoveryService.getHead(assetInvariant);
                runPredicate(context, assetInvariant, head);
                counter++;
            } catch (HEADNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find head for invariant: " + assetInvariant.toMultiHash());
            }

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

        InstrumentFactory.instance().measure(StatsTYPE.policies, "runPolicies - START - for context " + context.getName());

        Map<IGUID, ContextVersionInfo> contentsToProcess =  contextsContentsDirectory.getContentsThatPassedPredicateTestRows(context.guid(), false);
        contentsToProcess.forEach((guid, row) -> {

            if (row.predicateResult && !row.policySatisfied) {
                runPolicies(context, guid);
            }

        });

        InstrumentFactory.instance().measure(StatsTYPE.policies, "runPolicies - END - for context " + context.getName());
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
            SOS_LOG.log(LEVEL.WARN, "N/A yet - Check that policies are still valid");

            long start = System.currentTimeMillis();
            checkPolicies();
            long end = System.currentTimeMillis();
            checkPolicyThreadSessionStatistics.add(new Pair<>(start, end));

        }, checkPoliciesThreadSettings.getInitialDelay(), checkPoliciesThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    public void checkPolicies() {

        for (Context context : getContexts()) {
            checkPolicies(context);
        }
    }

    private void checkPolicies(Context context) {

        InstrumentFactory.instance().measure(StatsTYPE.checkPolicies, "checkPolicies - START - for context " + context.getName());

        Map<IGUID, ContextVersionInfo> contentsToProcess = contextsContentsDirectory.getContentsThatPassedPredicateTestRows(context.guid(), false);
        contentsToProcess.forEach((guid, row) -> {
            if (row.predicateResult) {

                SOS_LOG.log(LEVEL.INFO, "Check policies for context " + context.getName() + " and Version " + guid.toShortString());
                checkPolicies(context, guid);
                SOS_LOG.log(LEVEL.INFO, "ASYN Call - Finished to run CHECK policies for context " + context.getName() + " and Version " + guid.toShortString());
            }
        });

        InstrumentFactory.instance().measure(StatsTYPE.checkPolicies, "checkPolicies - END - for context " + context.getName());
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
     * @param assetInvariant
     * @param versionGUID to evaluate
     */
    private void runPredicate(Context context, IGUID assetInvariant, IGUID versionGUID) {

        IGUID contextGUID = context.guid();
        boolean alreadyRun =  contextsContentsDirectory.entryExists(contextGUID, versionGUID);
        boolean maxAgeExpired = false;

        if (alreadyRun) {
            maxAgeExpired = predicateHasExpired(context, versionGUID);
        }

        if (!alreadyRun || maxAgeExpired) {

            boolean passed = context.predicate().test(versionGUID);

            ContextVersionInfo content = new ContextVersionInfo();
            content.predicateResult = passed;
            content.timestamp = System.currentTimeMillis();

            for(IGUID version:dataDiscoveryService.getVersions(assetInvariant)) {

                if (!version.equals(versionGUID)) {
                    contextsContentsDirectory.evict(contextGUID, version);
                }
            }

            contextsContentsDirectory.addEntry(contextGUID, versionGUID, content);
        }

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
            ContextVersionInfo prev =  contextsContentsDirectory.getEntry(context.guid(), guid);

            // NOTE - this is a naive way to update only the policy result
            content.predicateResult = prev.predicateResult;
            content.timestamp = prev.timestamp;

            Policy[] policies = context.policies();
            boolean allPoliciesAreSatisfied = true;
            for (Policy policy:policies) {

                Manifest manifest = dataDiscoveryService.getManifest(guid);
                policy.apply(manifest);
                allPoliciesAreSatisfied = allPoliciesAreSatisfied && policy.satisfied(manifest);
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

            Policy[] policies = context.policies();
            boolean allPoliciesAreSatisfied = true;
            for (Policy policy:policies) {

                Manifest manifest = dataDiscoveryService.getManifest(guid);
                allPoliciesAreSatisfied = allPoliciesAreSatisfied && policy.satisfied(manifest);;
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

        long maxage = context.predicate().maxAge();
        long contentLastRun = content.timestamp;
        long now = System.currentTimeMillis();

        return (now - contentLastRun) > maxage;
    }

    private void loadContexts() throws ServiceException {

        try {
            for (IGUID contextsToLoad : localContextsDirectory.getContexts()) {

                Context context = localContextsDirectory.getContext(contextsToLoad);
                inMemoryCache.addContext(context);
            }

        } catch (DataStorageException | GUIDGenerationException | ContextNotFoundException e) {
            throw new ServiceException("ContextService - Unable to load contexts correctly");
        }
    }


}

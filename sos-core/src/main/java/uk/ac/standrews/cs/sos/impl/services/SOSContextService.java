package uk.ac.standrews.cs.sos.impl.services;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextContent;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsCacheImpl;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContents;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.services.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSContextService implements ContextService {

    private static final int CMS_SCHEDULER_PS = 4;

    private LocalStorage localStorage;
    private DataDiscoveryService dataDiscoveryService;

    private PolicyActions policyActions;

    // The inMemoryCache keeps the context objects for this node in memory.
    private ContextsCacheImpl inMemoryCache;
    private ContextsContents contextsContents; // TODO - rename to something more meaninful?

    // TODO - store contexts definitions in local storage

    // This executor service will be used to schedule any background tasks
    private ScheduledExecutorService service;

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
    public SOSContextService(LocalStorage localStorage, DataDiscoveryService dataDiscoveryService, NodeDiscoveryService nodeDiscoveryService, UsersRolesService usersRolesService, Storage storage) {

        this.localStorage = localStorage;
        this.dataDiscoveryService = dataDiscoveryService;

        inMemoryCache = new ContextsCacheImpl(); // TODO - load existing contexts into memory via reflection
        contextsContents = new ContextsContents(); // TODO - load mappings/indices

        policyActions = new PolicyActions(nodeDiscoveryService, dataDiscoveryService, usersRolesService, storage); // TODO - the policy language should be made available to all the context instances


        try {
            ContextLoader.LoadMultipleContexts("PATH TO CONTEXTS");
            // TODO - make instances for all loaded contexts
            // TODO - need to save GUIDs back to contexts (this is simply part of the JSON)

        } catch (ContextLoaderException e) {
            e.printStackTrace(); // TODO - proper exception
        }

        // Run background CRON Jobs if and only if this is set in the node settings file
        if (SOSLocalNode.settings.getServices().getCms().isAutomatic()) {

            service = new ScheduledThreadPoolExecutor(CMS_SCHEDULER_PS);
            runPredicatesPeriodic();
            runPoliciesPeriodic();
            checkPoliciesPeriodic();
            getDataPeriodic();
            spawnContextsPeriodic();
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

        return inMemoryCache.addContext(context);
    }

    @Override
    public IGUID addContext(String jsonContext) throws Exception {

        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(jsonContext);
        String contextName = jsonNode.get(CONTEXT_JSON_NAME).textValue();
        NodesCollection domain = makeNodesCollection(jsonNode, CONTEXT_JSON_DOMAIN);
        NodesCollection codomain = makeNodesCollection(jsonNode, CONTEXT_JSON_CODOMAIN);

        ContextLoader.LoadContext(jsonNode);
        Context context = ContextLoader.Instance(contextName, policyActions, contextName, domain, codomain);

        return addContext(context);
    }

    @Override
    public IGUID addContext(File file) throws Exception {

        JsonNode node = JSONHelper.JsonObjMapper().readTree(file);
        return addContext(node.toString());
    }

    @Override
    public Context getContext(IGUID contextGUID) throws ContextNotFoundException {

        return inMemoryCache.getContext(contextGUID);
    }

    @Override
    public Context getContext(String contextName) throws ContextNotFoundException {
        // TODO
        return null;
    }

    @Override
    public Set<IGUID> getContents(IGUID context) {

        return contextsContents.getContentsThatPassedPredicateTest(context);
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();

            IFile contextsContents = localStorage.createFile(cacheDir, CMS_INDEX_FILE);
            this.contextsContents.persist(contextsContents);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the CMS index");
        }

        if (service != null) {
            service.shutdown();
        }
    }

    ////////////////////
    // PERIODIC TASKS //
    ////////////////////

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
            runPredicates();

        }, predicateThreadSettings.getInitialDelay(), predicateThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    public int runPredicates() {

        int counter = 0;

        for (Context context : getContexts()) {
            InstrumentFactory.instance().measure(StatsTYPE.predicate, "runPredicates - START - for context " + context.getName());

            for (IGUID assetInvariant : dataDiscoveryService.getAllAssets()) {

                try {
                    IGUID head = dataDiscoveryService.getHead(assetInvariant);

                    SOS_LOG.log(LEVEL.INFO, "Running predicate for context " + context.getName() + " and Version-HEAD " + head.toShortString());
                    runPredicate(context, assetInvariant, head);
                    counter++;

                    SOS_LOG.log(LEVEL.INFO, "Finished to run predicate for context " + context.getName() + " and Version-HEAD " + head.toShortString());
                } catch (HEADNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find head for invariant");
                }

            }

            InstrumentFactory.instance().measure(StatsTYPE.predicate, "runPredicates - END - for context " + context.getName());
        }


        return counter;
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

        InstrumentFactory.instance().measure(StatsTYPE.policies, "runPolicies - START");
        SettingsConfiguration.Settings.ThreadSettings policiesThreadSettings = SOSLocalNode.settings.getServices().getCms().getPoliciesThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running policies - this is a periodic background thread");

            for (Context context : getContexts()) {

                Map<IGUID, ContextContent> contentsToProcess = contextsContents.getContentsThatPassedPredicateTestRows(context.guid());
                contentsToProcess.forEach((guid, row) -> {
                    if (row.predicateResult && !row.policySatisfied) {

                        SOS_LOG.log(LEVEL.INFO, "Running policies for context " + context.getName() + " and Version " + guid.toShortString());
                        runPolicies(context, guid);
                        SOS_LOG.log(LEVEL.INFO, "ASYN Call - Finished to run policies for context " + context.getName() + " and Version " + guid.toShortString());
                    }
                });

            }

        }, policiesThreadSettings.getInitialDelay(), policiesThreadSettings.getPeriod(), TimeUnit.SECONDS);

        InstrumentFactory.instance().measure(StatsTYPE.policies, "runPolicies - END");
    }

    private void checkPoliciesPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings checkPoliciesThreadSettings = SOSLocalNode.settings.getServices().getCms().getCheckPoliciesThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.WARN, "N/A yet - Check that policies are still valid");

            for (Context context : getContexts()) {

                Map<IGUID, ContextContent> contentsToProcess = contextsContents.getContentsThatPassedPredicateTestRows(context.guid());
                contentsToProcess.forEach((guid, row) -> {
                    if (row.predicateResult) {

                        SOS_LOG.log(LEVEL.INFO, "Check policies for context " + context.getName() + " and Version " + guid.toShortString());
                        checkPolicies(context, guid);
                        SOS_LOG.log(LEVEL.INFO, "ASYN Call - Finished to run CHECK policies for context " + context.getName() + " and Version " + guid.toShortString());
                    }
                });

            }

        }, checkPoliciesThreadSettings.getInitialDelay(), checkPoliciesThreadSettings.getPeriod(), TimeUnit.SECONDS);
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

                NodesCollection domain = context.domain();

                // First thing to do is to get the GUIDs for that domain
                // TODO - Not sure how to deal with this, as the domain might be huge

                // TODO - before downloading data, we should check the following:
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

            // Get contexts that have to be spawned
            // spawn contexts

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

        boolean alreadyRun = contextsContents.hasBeenProcessed(contextGUID, versionGUID);
        boolean maxAgeExpired = false;

        if (alreadyRun) {
            maxAgeExpired = predicateHasExpired(context, versionGUID);
        }

        if (!alreadyRun || maxAgeExpired) {

            boolean passed = context.predicate().test(versionGUID);
            SOS_LOG.log(LEVEL.INFO, "Context " + context.getName() + " for version " + versionGUID + " has passed: " + passed);

            ContextContent content = new ContextContent();
            content.predicateResult = passed;
            content.timestamp = System.nanoTime();

            // TODO - if there is another entry for this asset, then remove it

            contextsContents.addUpdateMapping(contextGUID, versionGUID, content);
        }

    }

    /**
     * Run the policies of a given context for the specified entity
     *
     * @param context
     * @param guid of the entity
     */
    private void runPolicies(Context context, IGUID guid) {

        try {
            Policy[] policies = context.policies();
            for (Policy policy:policies) {

                Manifest manifest = dataDiscoveryService.getManifest(guid);
                policy.apply(manifest);
                boolean policyIsSatisfied = policy.satisfied(manifest);

                // TODO - this is a naive way to update only the policy result
                ContextContent prev = contextsContents.get(context.guid(), guid);
                ContextContent content = new ContextContent();
                content.predicateResult = prev.predicateResult;
                content.timestamp = prev.timestamp;
                content.policySatisfied = policyIsSatisfied;

                contextsContents.addUpdateMapping(context.guid(), guid, content);
            }

        } catch (ManifestNotFoundException | PolicyException e) {
            e.printStackTrace();
        }
    }

    private void checkPolicies(Context context, IGUID guid) {

        try {
            Policy[] policies = context.policies();
            for (Policy policy:policies) {

                Manifest manifest = dataDiscoveryService.getManifest(guid);
                boolean policyIsSatisfied = policy.satisfied(manifest);

                // TODO - this is a naive way to update only the policy result
                ContextContent prev = contextsContents.get(context.guid(), guid);
                ContextContent content = new ContextContent();
                content.predicateResult = prev.predicateResult;
                content.timestamp = prev.timestamp;
                content.policySatisfied = policyIsSatisfied;

                contextsContents.addUpdateMapping(context.guid(), guid, content);
            }

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
        ContextContent content = contextsContents.get(context.guid(), versionGUID);

        long maxage = context.predicate().maxAge();
        long contentLastRun = content.timestamp;
        long now = System.nanoTime();

        return (now - contentLastRun) > maxage;
    }

    private NodesCollection makeNodesCollection(JsonNode jsonNode, String tag) throws GUIDGenerationException, NodeNotFoundException, NodesCollectionException {
        NodesCollection retval;
        NodesCollection.TYPE type = NodesCollection.TYPE.LOCAL;
        Set<IGUID> nodes = new LinkedHashSet<>();
        if (jsonNode.has(tag)) {
            type = NodesCollection.TYPE.valueOf(jsonNode.get(tag).get("type").asText());
            JsonNode nodeRefs = jsonNode.get(tag).get("nodes");

            for(JsonNode nodeRef:nodeRefs) {
                IGUID ref = GUIDFactory.recreateGUID(nodeRef.asText());
                nodes.add(ref);
            }

            retval = new NodesCollectionImpl(type, nodes);
        } else {
            retval = new NodesCollectionImpl(type);
        }

        return retval;
    }
}

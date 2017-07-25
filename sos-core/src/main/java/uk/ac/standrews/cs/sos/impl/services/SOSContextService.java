package uk.ac.standrews.cs.sos.impl.services;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextContent;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsCacheImpl;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContents;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.Instrument;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.services.*;
import uk.ac.standrews.cs.sos.services.experiments.ContextServiceExperiment;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSContextService implements ContextService, ContextServiceExperiment {

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
        String contextName = jsonNode.get("name").textValue();

        ContextLoader.LoadContext(jsonNode);
        Context context = ContextLoader.Instance(contextName, policyActions, contextName, new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        return addContext(context);
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

        // FIXME - this should work only if in experiment mode!!!!
        Instrument.instance().measure(StatsTYPE.predicate, "runPredicates - START");

        int counter = 0;

        for (Context context : getContexts()) {
            for (IGUID assetInvariant : dataDiscoveryService.getAllAssets()) {

                try {
                    IGUID head = dataDiscoveryService.getHead(assetInvariant);

                    SOS_LOG.log(LEVEL.INFO, "Running predicate for context " + context.guid() + " and Version-HEAD " + head.toString());
                    runPredicate(context, head);
                    counter++;

                    SOS_LOG.log(LEVEL.INFO, "Finished to run predicate for context " + context.guid() + " and Version-HEAD " + head.toString());
                } catch (HEADNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find head for invariant");
                }

            }
        }

        // FIXME - this should work only if in experiment mode!!!!
        Instrument.instance().measure(StatsTYPE.predicate, "runPredicates - END");

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

        SettingsConfiguration.Settings.ThreadSettings policiesThreadSettings = SOSLocalNode.settings.getServices().getCms().getPoliciesThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running policies - this is a periodic background thread");

            for (IGUID contextGUID : inMemoryCache.getContexts()) {

                Map<IGUID, ContextContent> contentsToProcess = contextsContents.getContentsThatPassedPredicateTestRows(contextGUID);
                contentsToProcess.forEach((guid, row) -> {
                    if (row.predicateResult && !row.policySatisfied) {

                        SOS_LOG.log(LEVEL.INFO, "Running policies for context " + contextGUID + " and Version " + guid);
                        runPolicies(contextGUID, guid);
                        SOS_LOG.log(LEVEL.INFO, "ASYN Call - Finished to run policies for context " + contextGUID + " and Version " + guid);
                    }
                });

            }

        }, policiesThreadSettings.getInitialDelay(), policiesThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }

    private void checkPoliciesPeriodic() {

        SettingsConfiguration.Settings.ThreadSettings checkPoliciesThreadSettings = SOSLocalNode.settings.getServices().getCms().getCheckPoliciesThread();

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.WARN, "N/A yet - Check that policies are still valid");

            // TODO - work in progress

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

            // TODO - iterate over context
            // TODO - for each context, context sources

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
     * @param versionGUID to evaluate
     */
    private void runPredicate(Context context, IGUID versionGUID) {

        IGUID contextGUID = context.guid();

        boolean alreadyRun = contextsContents.hasBeenProcessed(contextGUID, versionGUID);
        boolean maxAgeExpired = false;

        if (alreadyRun) {
            maxAgeExpired = predicateHasExpired(context, versionGUID);
        }

        if (!alreadyRun || maxAgeExpired) {

            boolean passed = context.predicate().test(versionGUID);

            ContextContent content = new ContextContent();
            content.predicateResult = passed;
            content.timestamp = System.nanoTime();

            contextsContents.addMapping(contextGUID, versionGUID, content);
        }

    }

    /**
     * Run the policies of a given context for the specified entity
     *
     * @param contextGUID of the context
     * @param guid of the entity
     */
    private void runPolicies(IGUID contextGUID, IGUID guid) {

        try {
            Context context = getContext(contextGUID);

            Policy[] policies = context.policies();
            for (Policy policy:policies) {

                Manifest manifest = dataDiscoveryService.getManifest(guid);
                policy.apply(manifest);

                SOS_LOG.log(LEVEL.WARN, "Policy result should be updated for context " + contextGUID + " and content " + guid);
                // TODO - update contextsContents
            }
        } catch (ContextNotFoundException | ManifestNotFoundException | PolicyException e) {
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
}

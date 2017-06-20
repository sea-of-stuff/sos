package uk.ac.standrews.cs.sos.impl.actors;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.*;
import uk.ac.standrews.cs.sos.constants.Threads;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextContent;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsCacheImpl;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContents;
import uk.ac.standrews.cs.sos.impl.context.examples.BinaryReplicationContext;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;
import static uk.ac.standrews.cs.sos.constants.Threads.*;

/**
 * The SOSContextService managed the contexts for this node.
 *
 * NOTE - IDEA should have a lock on content (e.g. this content is being managed by this policy for the moment, thus halt)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSContextService implements ContextService {

    private LocalStorage localStorage;
    private DataDiscoveryService dataDiscoveryService;

    private PolicyActions policyActions;

    // The inMemoryCache keeps the context objects for this node in memory.
    private ContextsCacheImpl inMemoryCache;
    private ContextsContents contextsContents;

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
            e.printStackTrace();
        }

        // FIXME - The following is an hardcoded context, which should instead be loaded from disk
        try {


            Context binaryReplicationContext = new BinaryReplicationContext(policyActions,
                    "binary replication context",
                    new NodesCollectionImpl(NodesCollection.TYPE.LOCAL),
                    new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

            addContext(binaryReplicationContext);
        } catch (Exception e) {
            e.printStackTrace(); // TODO - better exception handling
        }

        // Background CRON processes
        service = new ScheduledThreadPoolExecutor(Threads.CMS_SCHEDULER_PS);
        runPredicatesPeriodic();
        runPoliciesPeriodic();

        getDataPeriodic();
        spawnContextsPeriodic();
    }

    @Override
    public Set<Context> getContexts() {

        return inMemoryCache.getContexts().stream().map(c -> {
            try {
                return getContext(c);
            } catch (ContextNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public void addContext(Context context) throws Exception {

        inMemoryCache.addContext(context);
    }

    @Override
    public void addContext(String jsonContext) throws Exception {

        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(jsonContext);
        String contextName = jsonNode.get("name").textValue();

        ContextLoader.LoadContext(jsonNode);
        Context context = ContextLoader.Instance(contextName, policyActions, contextName, new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));
        addContext(context);

    }

    @Override
    public Context getContext(IGUID contextGUID) throws ContextNotFoundException {

        return inMemoryCache.getContext(contextGUID);
    }

    @Override
    public Set<IGUID> getContents(IGUID context) {

        return contextsContents.getContents(context);
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

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running predicates - this is a periodic background thread");

            for (IGUID iguid : inMemoryCache.getContexts()) {

                try {
                    Context context = getContext(iguid);
                    for (IGUID assetInvariant : dataDiscoveryService.getAllAssets()) {

                        for (IGUID tip : dataDiscoveryService.getTips(assetInvariant)) {

                            SOS_LOG.log(LEVEL.INFO, "Running predicate for context " + context.guid() + " and Version-HEAD " + tip.toString());
                            runPredicate(context, tip);
                        }
                    }

                } catch (ContextNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find context");
                } catch (TIPNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find head for invariant");
                }

            }

        }, PREDICATE_PERIODIC_INIT_DELAY_S, PREDICATE_PERIODIC_DELAY_S, TimeUnit.SECONDS);

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

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running policies - this is a periodic background thread");

            for (IGUID contextGUID : inMemoryCache.getContexts()) {

                HashMap<IGUID, ContextContent> contentsToProcess = contextsContents.getContentsRows(contextGUID);
                contentsToProcess.forEach((guid, row) -> {
                    if (row.predicateResult && !row.policySatisfied) {
                        runPolicies(contextGUID, guid);
                    }
                });

            }

        }, POLICIES_PERIODIC_INIT_DELAY_S, POLICIES_PERIODIC_DELAY_S, TimeUnit.SECONDS);
    }

    /**
     * Periodically get data (or references?) from other nodes
     * as specified by the sources of a context
     */
    private void getDataPeriodic() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.WARN, "N/A yet - Get data from other nodes - this is a periodic background thread");

            // TODO - iterate over context
            // TODO - for each context, context sources

        }, GET_DATA_PERIODIC_INIT_DELAY_S, GET_DATA_PERIODIC_DELAY_S, TimeUnit.SECONDS);
    }

    /**
     * Periodically spawn/replicate contexts to other nodes
     */
    private void spawnContextsPeriodic() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.WARN, "N/A yet - Spawn contexts to other nodes - this is a periodic background thread");

            // Get contexts that have to be spawned
            // spawn contexts

        }, SPAWN_PERIODIC_INIT_DELAY_S, SPAWN_PERIODIC_DELAY_S, TimeUnit.SECONDS);
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
     * @param context
     * @param versionGUID
     * @return
     */
    private void runPredicate(Context context, IGUID versionGUID) {

        IGUID contextGUID = context.guid();

        boolean alreadyRun = contextsContents.contentProcessedForContext(contextGUID, versionGUID);
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
     * @param context
     * @param versionGUID
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
package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.*;
import uk.ac.standrews.cs.sos.constants.Threads;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.PolicyLanguage;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextContent;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsCacheImpl;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContents;
import uk.ac.standrews.cs.sos.impl.context.examples.BinaryReplicationContext;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;
import static uk.ac.standrews.cs.sos.constants.Threads.*;

/**
 * TODO - should have a lock on content (e.g. this content is being managed by this policy for the moment, thus halt)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSContextService implements ContextService {

    private LocalStorage localStorage;
    private DataDiscoveryService dataDiscoveryService;

    private PolicyLanguage policyLanguage;

    // The inMemoryCache keeps the context objects for this node in memory.
    // TODO - store contexts definitions in local storage
    private ContextsCacheImpl inMemoryCache;
    private ContextsContents contextsContents;

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
     * @param storage used to access the SOS' storage
     */
    public SOSContextService(LocalStorage localStorage, DataDiscoveryService dataDiscoveryService,
                             NodeDiscoveryService nodeDiscoveryService, UsersRolesService usersRolesService, Storage storage) {

        this.localStorage = localStorage;
        this.dataDiscoveryService = dataDiscoveryService;

        // TODO - load existing contexts into memory via reflection
        inMemoryCache = new ContextsCacheImpl();

        // TODO - load mappings/indices
        contextsContents = new ContextsContents();

        policyLanguage = new PolicyLanguage(nodeDiscoveryService, dataDiscoveryService, usersRolesService, storage);


        // FIXME - do not hardcode this as contexts should be loaded from disk (see comments above)
        try {
            Context binaryReplicationContext = new BinaryReplicationContext(policyLanguage, "binary replication context",
                    new NodesCollectionImpl(NodesCollection.TYPE.LOCAL),
                    new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

            addContext(binaryReplicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Background processes
        service = new ScheduledThreadPoolExecutor(Threads.CMS_SCHEDULER_PS);
        getDataPeriodic();
        spawnContextsPeriodic();
        runPredicatesPeriodic();
        runPoliciesPeriodic();
    }

    @Override
    public void addContext(Context context) throws Exception {

        inMemoryCache.addContext(context);
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
     * Iterate over all activeRole contexts.
     * For each context:
     * Run all known versions against the predicate of the context.
     *
     */
    private void runPredicatesPeriodic() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running predicates - this is a periodic background thread");

            Iterator<IGUID> it = inMemoryCache.getContexts();
            while (it.hasNext()) {

                try {
                    Context context = getContext(it.next());
                    for(IGUID assetInvariant : dataDiscoveryService.getAllAssets()) {

                        for(IGUID head : dataDiscoveryService.getHeads(assetInvariant)) {

                            runPredicate(context, head);
                        }
                    }

                } catch (ContextNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find context");
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

            Iterator<IGUID> it = inMemoryCache.getContexts();
            while (it.hasNext()) {

                IGUID contextGUID = it.next();

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
            SOS_LOG.log(LEVEL.INFO, "N/A yet - Get data from other nodes - this is a periodic background thread");

            // TODO - iterate over context
            // TODO - for each context, context sources

        }, GET_DATA_PERIODIC_INIT_DELAY_S, GET_DATA_PERIODIC_DELAY_S, TimeUnit.SECONDS);
    }

    /**
     * Periodically spawn/replicate contexts to other nodes
     */
    private void spawnContextsPeriodic() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "N/A yet - Spawn contexts to other nodes - this is a periodic background thread");

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

            ContextContent content = contextsContents.get(contextGUID, versionGUID);

            long maxage = context.predicate().maxAge();
            long contentLastRun = content.timestamp;
            long now = System.nanoTime();

            maxAgeExpired = (now - contentLastRun) > maxage;

        }

        if (!alreadyRun && !maxAgeExpired) {

            boolean passed = context.predicate().test(versionGUID);

            ContextContent content = new ContextContent();
            content.predicateResult = passed;
            content.timestamp = System.nanoTime();
            
            contextsContents.addMapping(contextGUID, versionGUID, content);
        }

    }

    private void runPolicies(IGUID contextGUID, IGUID guid) {

        try {
            Context context = getContext(contextGUID);

            Policy[] policies = context.policies();
            for (Policy policy:policies) {

                Manifest manifest = dataDiscoveryService.getManifest(guid);
                policy.apply(manifest);

                System.out.println("Policy result should be updated for context " + contextGUID + " and content " + guid);
                // TODO - update contextsContents
            }
        } catch (ContextNotFoundException | ManifestNotFoundException | PolicyException e) {
            e.printStackTrace();
        }
    }

}

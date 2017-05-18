package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.CMS;
import uk.ac.standrews.cs.sos.actors.DDS;
import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.constants.Threads;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.PolicyLanguage;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextContent;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsCacheImpl;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextsContents;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;

/**
 * TODO - should have a default scope
 * TODO - should have a lock on content (e.g. this content is being managed by this policy for the moment, thus halt)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCMS implements CMS {

    private LocalStorage localStorage;
    private DDS dds;

    private PolicyLanguage policyLanguage;

    private ContextsCacheImpl cache; // TODO - store contexts definitions in local storage
    private ContextsContents contextsContents;

    // This executor service will be used to schedule any background tasks
    private ScheduledExecutorService service;

    /**
     * Build a CMS instance.
     * The DDS is passed as parameter and it is needed to access the manifests to be processed.
     *
     * @param localStorage used to persist the internal data structures
     */
    public SOSCMS(LocalStorage localStorage, DDS dds, NDS nds, UsersRolesService usersRolesService) {
        this.localStorage = localStorage;
        this.dds = dds;

        // TODO - load existing contexts into memory via reflection
        cache = new ContextsCacheImpl();

        // TODO - load mappings/indices
        contextsContents = new ContextsContents();

        policyLanguage = new PolicyLanguage(nds, dds, usersRolesService);

        // Background processes
        service = new ScheduledThreadPoolExecutor(Threads.CMS_SCHEDULER_PS);
        getDataPeriodic();
        spawnContextsPeriodic();
        runPredicatesPeriodic();
        runPoliciesPeriodic();
    }

    @Override
    public void addContext(Context context) throws Exception {

        cache.addContext(context);
    }

    @Override
    public Context getContext(IGUID contextGUID) throws ContextNotFoundException {

        return cache.getContext(contextGUID);
    }

    // TODO - do not use iterator
    @Override
    public Iterator<IGUID> getContents(IGUID context) {

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
     * Run all known versions against the predicate of the context.
     *
     */
    private void runPredicatesPeriodic() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running predicates - this is a periodic background thread");

            Iterator<IGUID> it = cache.getContexts();
            while (it.hasNext()) {

                try {
                    Context context = getContext(it.next());
                    for(Version version : dds.getAllVersions()) { // FIXME - get only heads?
                        runPredicate(context, version);
                    }

                } catch (ContextNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find context");
                }

            }

        }, 30, 60, TimeUnit.SECONDS);

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

            Iterator<IGUID> it = cache.getContexts();
            while (it.hasNext()) {

                IGUID contextGUID = it.next();

                HashMap<IGUID, ContextContent> contentsToProcess = contextsContents.getContentsRows(contextGUID);
                contentsToProcess.forEach((guid, row) -> {
                    if (row.predicateResult && !row.policySatisfied) {
                        runPolicies(contextGUID, guid);
                    }
                });

            }

        }, 45, 60, TimeUnit.SECONDS);
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

        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Periodically spawn/replicate contexts to other nodes
     */
    private void spawnContextsPeriodic() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "N/A yet - Spawn contexts to other nodes - this is a periodic background thread");

            // Get contexts that have to be spawned
            // spawn contexts

        }, 1, 1, TimeUnit.MINUTES);
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
     * @param version
     * @return
     */
    private void runPredicate(Context context, Version version) {

        IGUID versionGUID = version.guid();

        boolean alreadyRun = contextsContents.contentProcessedForContext(context.guid(), versionGUID);
        boolean maxAgeExpired = false;

        if (alreadyRun) {

            ContextContent content = contextsContents.get(context.guid(), versionGUID);

            long maxage = context.predicate().maxAge();
            long contentLastRun = content.timestamp;
            long now = System.nanoTime();

            if (now - contentLastRun > maxage) {
                maxAgeExpired = true;
            }

        }

        if (!alreadyRun && !maxAgeExpired) {

            boolean passed = context.predicate().test(versionGUID);

            ContextContent content = new ContextContent();
            content.predicateResult = passed;
            content.timestamp = System.nanoTime();
            
            contextsContents.addMapping(context.guid(), versionGUID, content);
        }

    }

    private void runPolicies(IGUID contextGUID, IGUID guid) {

        try {
            Context context = getContext(contextGUID);

            Policy[] policies = context.policies();
            for (Policy policy:policies) {

                Manifest manifest = dds.getManifest(guid);
                policy.apply(manifest);

                System.out.println("Policy result should be updated for context " + contextGUID + " and content " + guid);
                // TODO - update contextsContents
            }
        } catch (ContextNotFoundException | ManifestNotFoundException | PolicyException e) {
            e.printStackTrace();
        }
    }

}

package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.CMS;
import uk.ac.standrews.cs.sos.actors.DDS;
import uk.ac.standrews.cs.sos.constants.Threads;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.ContextsCacheImpl;
import uk.ac.standrews.cs.sos.impl.context.ContextsContents;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Scope;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;

/**
 * TODO - versioning of contexts
 * TODO - should have a default scope
 * TODO - should have a lock on content (e.g. this content is being managed by this policy, thus halt)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCMS implements CMS {

    private LocalStorage localStorage;
    private DDS dds;

    private ContextsCacheImpl cache;
    // TODO - local + remote
    private ContextsContents contextsContents;

    // This executor service will be used to schedule any background tasks
    private ScheduledExecutorService service;

    /**
     * Build a CMS instance.
     * The DDS is passed as parameter and it is needed to access the manifests to be processed.
     *
     * @param localStorage used to persist the internal data structures
     */
    public SOSCMS(LocalStorage localStorage, DDS dds) {
        this.localStorage = localStorage;
        this.dds = dds;

        // TODO - load existing contexts into memory via reflection
        cache = new ContextsCacheImpl();

        // TODO - load mappings/indices
        contextsContents = new ContextsContents();

        // Background processes
        service = new ScheduledThreadPoolExecutor(Threads.CMS_SCHEDULER_PS);
        getData();
        spawnContexts();
        runPredicates();
        runPolicies();
    }

    @Override
    public void addContext(IGUID scope, Context context) throws Exception {

        cache.addContext(context);
        contextsContents.addContextScope(context.guid(), scope);
    }

    @Override
    public Context getContext(IGUID contextGUID) throws ContextNotFoundException {

        return cache.getContext(contextGUID);
    }

    /**
     * Return the context to which this version belongs to
     * @param version
     * @return
     */
    private Set<IGUID> runPredicates(Version version) {

        Set<IGUID> contexts = new HashSet<>();

        Iterator<IGUID> it = cache.getContexts();
        while (it.hasNext()) {
            IGUID v = it.next();

            try {
                Context context = getContext(v);
                runPredicate(v, context, version);

                contexts.add(context.guid());

            } catch (ContextNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find context from version " + v);
            }

        }

        return contexts;
    }

    @Override
    public Iterator<IGUID> getContents(IGUID context) {

        return contextsContents.getContents(context);
    }

    @Override
    public void addScope(Scope scope) {

        cache.addScope(scope);
    }

    @Override
    public Scope getScope(IGUID guid) {

        return cache.getScope(guid);
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();

            IFile cacheFile = localStorage.createFile(cacheDir, CMS_INDEX_FILE);
            // TODO - cache.persist(cacheFile); <-- persist method of this class/index class

        } catch (DataStorageException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the CMS index");
        }
    }

    ////////////////////
    // PERIODIC TASKS //
    ////////////////////

    /**
     * Periodically get data (or references?) from other nodes
     * as specified by the sources of a context
     *
     */
    private void getData() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Get data from other nodes");

            // TODO - iterate over context
            // TODO - for each context, context sources

        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Periodically spawn/replicate contexts to other nodes
     */
    private void spawnContexts() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Spawn contexts to other nodes");

            // Get contexts that have to be spawned
            // spawn contexts

        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Run PERIODIC predicates
     */
    private void runPredicates() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running periodic predicates");

            Iterator<IGUID> it = cache.getContexts();
            while (it.hasNext()) {
                IGUID contextVersion = it.next();

                try {
                    Context context = getContext(contextVersion);
                    for(Version version : dds.getAllVersions()) { // FIXME - we actually need the dds to get the actual content to be processed
                        runPredicate(contextVersion, context, version);
                    }

                } catch (ContextNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find context from version " + contextVersion);
                }

            }

        }, 1, 1, TimeUnit.MINUTES);

    }

    private boolean runPredicate(IGUID contextVersion, Context context, Version version) {

        IGUID versionGUID = version.guid();

        boolean retval = false;

        boolean alreadyRun = contextsContents.has(contextVersion, versionGUID);
        boolean maxAgeExpired = false;

        if (alreadyRun) {

            ContextsContents.Row row = contextsContents.get(contextVersion, versionGUID);
            retval = row.predicateResult;

            long maxage = context.predicate().maxAge();

            // TODO - diff timetamp, maxage, now
        }

        if (!alreadyRun && !maxAgeExpired) {

            boolean passed = context.predicate().test(versionGUID);
            retval = passed;
            if (passed) {
                contextsContents.addMapping(contextVersion, versionGUID);
            }
        }

        return retval;
    }

    private void runPolicies() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running policies");

        }, 1, 10, TimeUnit.MINUTES);
    }

}

package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.CMS;
import uk.ac.standrews.cs.sos.actors.DDS;
import uk.ac.standrews.cs.sos.constants.Threads;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;

/**
 * TODO - manage scope too
 * TODO - add concept of persistence
 * TODO - should have a lock on content (e.g. this content is being managed by this policy, thus halt)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCMS implements CMS {

    private LocalStorage localStorage;
    private DDS dds;
    private HashMap<PredicateComputationType, List<IGUID>> contextsByPredicateType;

    // Maps the context to the versions belonging to it
    private HashMap<IGUID, List<IGUID>> mappings;
    // TODO - map of versions that failed to pass tests. Needed to avoid calculating the predicate again

    // This executor service will be used to schedule any background tasks
    private ScheduledExecutorService service;

    /**
     * Build a CMS instance.
     * The DDS is passed as parameter and it is needed to update the node about manifest-related information
     *
     * @param dds
     */
    public SOSCMS(LocalStorage localStorage, DDS dds) {
        this.localStorage = localStorage;
        this.dds = dds;

        initialiseMappings();

        // TODO - load existing contexts into memory via reflection
        // TODO - load mappings/indices

        service = new ScheduledThreadPoolExecutor(Threads.CMS_SCHEDULER_PS);

        // Start background processes
        getData();
        spawnContexts();
        runPredicates();
        runPolicies();
        checkPolicies();
    }

    private void initialiseMappings() {
        contextsByPredicateType = new HashMap<>();
        contextsByPredicateType.put(PredicateComputationType.BEFORE_STORING, new LinkedList<>());
        contextsByPredicateType.put(PredicateComputationType.AFTER_STORING, new LinkedList<>());
        contextsByPredicateType.put(PredicateComputationType.PERIODICALLY, new LinkedList<>());
        contextsByPredicateType.put(PredicateComputationType.AFTER_READING, new LinkedList<>());

        mappings = new HashMap<>();
    }

    @Override
    public Version addContext(Context context) throws Exception {

        try {
            Version version = ManifestFactory.createVersionManifest(context.guid(), null, null, null, null);

            dds.addManifest(context);
            dds.addManifest(version);

            contextsByPredicateType.get(context.predicate().predicateComputationType()).add(version.guid());

            return version;
        } catch (ManifestPersistException e) {
            throw new ContextException(e);
        }
    }

    @Override
    public Context getContext(IGUID version) throws ContextNotFoundException {

        try {
            Manifest manifest = dds.getManifest(version);
            return (Context) dds.getManifest(((Version) manifest).getContentGUID());
        } catch (ManifestNotFoundException e) {
            throw new ContextNotFoundException(e);
        }
    }

    @Override
    public Iterator<IGUID> getContexts(PredicateComputationType type) {

        return contextsByPredicateType.get(type).iterator();
    }

    @Override
    public void addMapping(IGUID context, IGUID version) {
        mappings.get(context).add(version);

        // TODO - persist
    }

    @Override
    public Set<IGUID> runPredicates(PredicateComputationType type, Version version) {

        Set<IGUID> contexts = new HashSet<>();

        Iterator<IGUID> it = getContexts(type);
        while (it.hasNext()) {
            IGUID v = it.next();

            try {
                Context context = getContext(v);
                runPredicate(v, context, version);

                contexts.add(context.guid()); // FIXME - version of context?

                for(Policy policy:context.policies()) {
                    if (policy.computationType() == PolicyComputationType.AFTER_PREDICATE) {
                        policy.run(version);
                    }
                }

            } catch (ContextNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find context from version " + v);
            }

        }

        return contexts;
    }

    @Override
    public Set<IGUID> runPredicates(PredicateComputationType type, InputStream data) {

        Set<IGUID> contexts = new HashSet<>();

        return contexts;
    }

    @Override
    public Iterator<IGUID> getContents(IGUID context) {

        List<IGUID> contents = mappings.get(context);
        if (contents == null) {
            return Collections.emptyIterator();
        } else {
            return contents.iterator();
        }

    }

    @Override
    public void flush() {

        try {
            Directory cacheDir = localStorage.getNodeDirectory();

            File cacheFile = localStorage.createFile(cacheDir, CMS_INDEX_FILE);
            // TODO - cache.persist(cacheFile);

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
     * It should be possible to "remove" content that is not relevant anymore?
     *
     */
    private void getData() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Get data from other nodes");

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

            Iterator<IGUID> it = getContexts(PredicateComputationType.PERIODICALLY);
            while (it.hasNext()) {
                IGUID v = it.next();

                try {
                    Context context = getContext(v);
                    for(Version version : dds.getAllVersions()) { // TODO - get only CURRENT VERSIONS?
                        runPredicate(v, context, version);
                    }

                } catch (ContextNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find context from version " + v);
                }

            }

        }, 1, 1, TimeUnit.MINUTES);
    }

    private boolean runPredicate(IGUID contextVersion, Context context, Version version) {

        IGUID versionGUID = version.guid();

        boolean alreadyProcessed = mappings.get(contextVersion).contains(versionGUID);
        // TODO - check against MAX-AGE attribute
        boolean retval = alreadyProcessed;
        if (!alreadyProcessed) {

            boolean passed = context.predicate().test(versionGUID);
            retval = passed;
            if (passed) {
                addMapping(contextVersion, versionGUID);
            }
        }

        return retval;
    }

    private void runPolicies() {
        // TODO - how does this differ from checkPolicies() ?
    }

    private void checkPolicies() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Checking that policies are satisfied. Otherwise, invalidate and re-run the policies");
            // TODO - check policies are satisfied. if not, attempt to re-satisfy them

        }, 1, 10, TimeUnit.MINUTES);

    }
}

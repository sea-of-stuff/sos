package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
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
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Scope;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;

/**
 * TODO - manage scope too
 * TODO - should have a default scope
 * TODO - add concept of persistence
 * TODO - should have a lock on content (e.g. this content is being managed by this policy, thus halt)
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCMS implements CMS, Serializable {

    private transient LocalStorage localStorage;
    private transient DDS dds;

    // TODO - separate Data structures from CMS

    // GUID --> Context
    private transient HashMap<IGUID, Context> contexts;

    // GUID --> Scope
    private transient HashMap<IGUID, Scope> scopes;

    // Assign a context to a scope
    // Context GUID --> Scope GUID
    private transient HashMap<IGUID, IGUID> contextsToScopes;

    // Maps the context to the versions belonging to it
    private transient HashMap<IGUID, HashMap<IGUID, CMSRow>> mappings;
    private class CMSRow {
        boolean predicateResult;
        long timestamp; // Time when the predicate was run for this content
        boolean policySatisfied; // Whether the policy has been satisfied or not
    }

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
    }

    private void initialiseMappings() {

        contexts = new HashMap<>();
        scopes = new HashMap<>();
        contextsToScopes = new HashMap<>();
        mappings = new HashMap<>();
    }

    @Override
    public Version addContext(Context context) throws Exception {

        try {
            Version version = ManifestFactory.createVersionManifest(context.guid(), null, null, null, null);

            dds.addManifest(context);
            dds.addManifest(version);

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

    private Iterator<IGUID> getContexts() {
        return contexts.keySet().iterator();
    }

    public void addMapping(IGUID context, IGUID version) {

        if (!mappings.containsKey(context)) {
            mappings.put(context, new HashMap<>());
        }

        mappings.get(context).put(version, new CMSRow());
    }

    public Set<IGUID> runPredicates(Version version) {

        Set<IGUID> contexts = new HashSet<>();

        Iterator<IGUID> it = getContexts();
        while (it.hasNext()) {
            IGUID v = it.next();

            try {
                Context context = getContext(v);
                runPredicate(v, context, version);

                contexts.add(context.guid()); // FIXME - version of context?

            } catch (ContextNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find context from version " + v);
            }

        }

        return contexts;
    }

    @Override
    public Iterator<IGUID> getContents(IGUID context) {

        HashMap<IGUID, CMSRow> contents = mappings.get(context);
        if (contents == null) {
            return Collections.emptyIterator();
        } else {
            return contents.keySet().iterator();
        }

    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();

            IFile cacheFile = localStorage.createFile(cacheDir, CMS_INDEX_FILE);
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

            Iterator<IGUID> it = getContexts();
            while (it.hasNext()) {
                IGUID contextVersion = it.next();

                try {
                    Context context = getContext(contextVersion);
                    for(Version version : dds.getAllVersions()) {
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
        boolean alreadyRun = mappings.get(contextVersion).containsKey(versionGUID);

        if (alreadyRun) {
            CMSRow row = mappings.get(contextVersion).get(versionGUID);
            retval = row.predicateResult;

            long maxage = context.predicate().max_age();

            // TODO - diff timetamp, maxage, now
        }

        if (!alreadyRun /* and over maxage */ ) {

            boolean passed = context.predicate().test(versionGUID);
            retval = passed;
            if (passed) {
                addMapping(contextVersion, versionGUID);
            }
        }

        return retval;
    }

    private void runPolicies() {

        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running policies");

        }, 1, 10, TimeUnit.MINUTES);
    }

    ///////////////////
    // Serialization //
    ///////////////////

    private void persist(IFile file) throws IOException {
        // TODO
    }

    public static SOSCMS load(IFile file) throws IOException, ClassNotFoundException {

        // TODO

        return null;
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        // TODO
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // TODO
    }

}

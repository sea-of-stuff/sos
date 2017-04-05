package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.CMS;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.model.*;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO - manage scope too
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCMS implements CMS {

    private static final int SCHEDULER_POOL_SIZE = 1;

    private DDS dds;
    private HashMap<PredicateComputationType, List<IGUID>> contextsByPredicateType;
    private HashMap<IGUID, List<IGUID>> mappings;

    public SOSCMS(DDS dds) {
        this.dds = dds;

        contextsByPredicateType = new HashMap<>();
        contextsByPredicateType.put(PredicateComputationType.BEFORE_STORING, new LinkedList<>());
        contextsByPredicateType.put(PredicateComputationType.AFTER_STORING, new LinkedList<>());
        contextsByPredicateType.put(PredicateComputationType.PERIODICALLY, new LinkedList<>());
        contextsByPredicateType.put(PredicateComputationType.AFTER_READING, new LinkedList<>());

        mappings = new HashMap<>();

        runPredicates();
        // TODO - replicate context to nodes within scope periodically
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
    public void runPredicates(PredicateComputationType type, Version version) {

        Iterator<IGUID> it = getContexts(type);
        while (it.hasNext()) {
            IGUID v = it.next();

            try {
                Context context = getContext(v);
                runPredicate(v, context, version);

                for(Policy policy:context.policies()) {
                    if (policy.computationType() == PolicyComputationType.AFTER_PREDICATE) {
                        policy.run(version);
                    }
                }

            } catch (ContextNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find context from version " + v);
            }

        }
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

    /**
     * Run PERIODIC predicates
     */
    private void runPredicates() {

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(SCHEDULER_POOL_SIZE);
        service.scheduleWithFixedDelay(() -> {
            SOS_LOG.log(LEVEL.INFO, "Running periodic predicates");

            Iterator<IGUID> it = getContexts(PredicateComputationType.PERIODICALLY);
            while (it.hasNext()) {
                IGUID v = it.next();

                try {
                    Context context = getContext(v);
                    for(Version version : dds.getAllAssets()) { // TODO - get only CURRENT VERSIONS?
                        runPredicate(v, context, version);
                    }

                } catch (ContextNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to find context from version " + v);
                }

            }

        }, 1, 1, TimeUnit.MINUTES);
    }

    private void runPredicate(IGUID contextVersion, Context context, Version version) {

        boolean alreadyProcessed = mappings.get(contextVersion).contains(version.guid());
        if (!alreadyProcessed) {

            boolean passed = context.predicate().test(version.guid()); // FIXME?
            if (passed) {
                mappings.get(contextVersion).add(version.guid());
            }
        }
    }
}

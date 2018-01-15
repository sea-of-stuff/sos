package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.TriggerPredicate;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Investigate the context performance as the cardinality of its domain changes
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_DO_1 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    // Must be static to be initialized before constructor
    private static String[] contextsToRun = new String[] {"predicate_1", "predicate_2", "predicate_3",
                                                         "predicate_4", "predicate_5", "predicate_6"};

    public Experiment_DO_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (String aContextsToRun : contextsToRun) {
                units.add(new ExperimentUnit_DO_1(aContextsToRun));
            }
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public ExperimentUnit getExperimentUnit() {

        return experimentUnitIterator.next();
    }

    @Override
    public int numberOfTotalIterations() {

        return experiment.getSetup().getIterations() * contextsToRun.length;
    }

    private class ExperimentUnit_DO_1 implements ExperimentUnit {

        private String contextFilename;
        private Context context;
        private List<IGUID> allVersions;

        private ContextService cms;

        ExperimentUnit_DO_1(String contextFilename) {
            this.contextFilename = contextFilename;
        }

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                cms = node.getCMS();

                System.out.println("Adding contexts to node");
                IGUID contextGUID = addContext(cms, experiment, contextFilename);

                System.out.println("Spawning context to nodes in domain. Context GUID: " + contextGUID.toMultiHash());
                context = cms.getContext(contextGUID);
                cms.spawnContext(context);

                System.out.println("Adding content to nodes");
                allVersions = distributeData(experiment, node, context, -1);

            } catch (ManifestPersistException | ContextException | IOException e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() throws ExperimentException {

            try {
                ExecutorService executorService = Executors.newFixedThreadPool(11); // 11 threads should be enough

                List<Callable<Object>> runnables = new LinkedList<>();
                runnables.add(triggerLocalPredicate());
                runnables.addAll(triggerRemotePredicate(context));

                System.out.println("Running Predicates");
                long start = System.nanoTime();
                executorService.invokeAll(runnables); // This method returns when all the calls finish
                long duration = System.nanoTime() - start;
                InstrumentFactory.instance().measure(StatsTYPE.predicate_remote, StatsTYPE.predicate_dataset, contextFilename, duration);

                executorService.shutdownNow();

            } catch (InterruptedException e) {
                throw new ExperimentException(e);
            }
        }

        @Override
        public void finish() throws ExperimentException {

            // Remove data from remote nodes
            deleteData(node, context, allVersions);

            // Remove context and context results from remote nodes
            deleteContext(node, context);
        }

        private Callable<Object> triggerLocalPredicate() {

            return () -> {
                int numberOfAssets = cms.runPredicates();
                System.out.println("Local predicate run over " + numberOfAssets + " assets"); // TODO - commentme
                return 0;
            };
        }

        private List<Callable<Object>> triggerRemotePredicate(Context context) {

            List<Callable<Object>> runnables = new LinkedList<>();
            NodesCollection domain = context.domain();
            for(IGUID nodeRef:domain.nodesRefs()) {
                runnables.add(triggerRemotePredicate(nodeRef, context.guid()));
            }

            return runnables;
        }

        private Callable<Object> triggerRemotePredicate(IGUID nodeRef, IGUID contextGUID) {

            return () -> {
                System.out.println("Running pred for node " + nodeRef.toMultiHash()); // TODO - commentme
                Node nodeToContext = node.getNDS().getNode(nodeRef);
                TriggerPredicate triggerPredicate = new TriggerPredicate(nodeToContext, contextGUID);

                TasksQueue.instance().performSyncTask(triggerPredicate);

                System.out.println("Finished to run pred for node " + nodeRef.toMultiHash() + " State: " + triggerPredicate.getState().name()); // TODO - commentme
                return 0;
            };
        }

    }

}

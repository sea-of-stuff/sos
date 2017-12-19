package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
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

import java.io.File;
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

    public Experiment_DO_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_DO_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_DO_1();
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations();
    }

    private class ExperimentUnit_DO_1 implements ExperimentUnit {

        private ContextService cms;

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                cms = node.getCMS();

                System.out.println("Adding content to node");

                // NOTE - Keep amount of data fixed
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                addFolderContentToNode(node, new File(datasetPath));

            } catch (Exception e) {
                throw new ExperimentException();
            }

            // TODO
            // data to add to nodes
            // datasets should be split as evenly as possible across nodes
        }

        @Override
        public void run() throws ExperimentException {

            try {
                String[] contextsToRun = new String[] {"predicate_1", "predicate_2"};

                for(String contextToRun:contextsToRun) {

                    System.out.println("Adding contexts to node");
                    IGUID contextGUID = addContext(cms, experiment, contextToRun);
                    Context context = cms.getContext(contextGUID);
                    System.out.println("Spawning context to nodes in domain");
                    cms.spawnContext(context);

                    ExecutorService executorService = Executors.newFixedThreadPool(11); // 11 threads should be enough

                    List<Callable<Object>> runnables = new LinkedList<>();
                    runnables.add(cms::runPredicates);
                    runnables.addAll(triggerRemotePredicate(context));

                    System.out.println("Running Predicates");
                    long start = System.nanoTime();
                    executorService.invokeAll(runnables); // This method returns when all the calls finish
                    long duration = System.nanoTime() - start;
                    InstrumentFactory.instance().measure(StatsTYPE.predicate_remote, StatsTYPE.predicate_dataset, contextToRun, duration);

                    executorService.shutdownNow();
                }

            } catch (ContextException | InterruptedException | ManifestPersistException e) {
                throw new ExperimentException(e);
            }
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
                System.out.println("Running pred for node " + nodeRef.toMultiHash());
                Node nodeToContext = node.getNDS().getNode(nodeRef);
                TriggerPredicate triggerPredicate = new TriggerPredicate(nodeToContext, contextGUID);

                TasksQueue.instance().performSyncTask(triggerPredicate);

                System.out.println("Finished to run pred for node " + nodeRef.toMultiHash() + " State: " + triggerPredicate.getState().name());
                return 0;
            };
        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "do_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_DO_1 experiment_do_1 = new Experiment_DO_1(experimentConfiguration);
        experiment_do_1.process();
    }

}

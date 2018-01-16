package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;

/**
 * NB stands for Normal Behaviours.
 *
 * TODO - Stats tracked:
 * - threads
 *  - context: predicate, policies, check_policies, context_spawning
 * - io
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_NB_1 extends BaseExperiment implements Experiment {

    public Experiment_NB_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_NB_1();
    }

    private class ExperimentUnit_NB_1 implements ExperimentUnit {

        private ContextService cms;

        @Override
        public void setup() throws ExperimentException {

            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                cms = node.getCMS();

                System.out.println("Adding content to node");
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                addFolderContentToNode(node, new File(datasetPath), -1);

                System.out.println("Adding contexts to node");
                addContexts();

                System.out.println("Running Predicates");
                cms.runPredicates();
            } catch (ContextException | IOException e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() throws ExperimentException {

            try {
                int seconds = 120;
                System.out.println("Running node in normal mode for " + seconds + " seconds");
                Thread.sleep(seconds * 1000);
                System.out.println("Finished running node in normal mode");
            } catch (InterruptedException e) {
                throw new ExperimentException(e);
            }
        }

        private void addContexts() throws ContextException {

            addContext(cms, experiment, "data_replication_1");
        }
    }

}

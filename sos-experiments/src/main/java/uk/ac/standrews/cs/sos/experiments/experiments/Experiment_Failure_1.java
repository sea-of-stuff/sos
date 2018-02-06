package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.experiments.protocol.ToggleRESTAPI;
import uk.ac.standrews.cs.sos.impl.node.BasicNode;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.File;
import java.io.IOException;

/**
 * Simulate failure by making remote nodes unresponsive to REST calls (except a special one that is needed to wake the node up again)
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_Failure_1 extends BaseExperiment implements Experiment {

    public Experiment_Failure_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_Failure_1();
    }

    private class ExperimentUnit_Failure_1 implements ExperimentUnit {

        private ContextService cms;

        @Override
        public void setup() throws ExperimentException {

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
                throw new ExperimentException(e);
            }
        }

        @Override
        public void run() throws ExperimentException {

            System.out.println("Running Policies");
            cms.runPolicies();

            // Disable REST API on remote node
            ExperimentConfiguration.Experiment.Node slaveNode = experiment.getNodes().iterator().next();
            Node remoteNode = new BasicNode(slaveNode.getSsh().getHost(), 8080);
            ToggleRESTAPI toggleRESTAPITask = new ToggleRESTAPI(remoteNode, true);
            TasksQueue.instance().performSyncTask(toggleRESTAPITask);
            // TODO - instrument time when node was disabled

            if (toggleRESTAPITask.getState() != TaskState.SUCCESSFUL) {
                throw new ExperimentException("Disable REST request was not successful");
            }

            // The check policy thread runs every 30 seconds.
            rest_a_bit(60 * 1000); // 1 minute

            writePolicyCheckStats();
        }

        private void addContexts() throws ContextException {

            addContext(cms, experiment, "data_replication_1");
        }


        private void writePolicyCheckStats() {

            for(Pair<Long, Integer> pair:cms.getValidPoliciesOverTime()) {

                InstrumentFactory.instance().measure(StatsTYPE.checkPolicies, StatsTYPE.no_valid_policies, "---", pair.X(), pair.Y());
            }
        }
    }
}

package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
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
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

/**
 * Basic skeleton for ExperimentFailure experiments
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_Failure extends BaseExperiment implements Experiment {

    protected Iterator<ExperimentUnit> experimentUnitIterator;

    Experiment_Failure(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return experimentUnitIterator.next();
    }

    public abstract class ExperimentUnit_Failure implements ExperimentUnit {

        protected int index;
        protected ContextService cms;

        ExperimentUnit_Failure(int index) {
            this.index = index;
        }

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
        public void finish() throws ExperimentException {

            // Re-enable REST API on all remote nodes
            for (ExperimentConfiguration.Experiment.Node slaveNode : experiment.getNodes()) {
                Node remoteNode = new BasicNode(slaveNode.getSsh().getHost(), 8080);
                ToggleRESTAPI toggleRESTAPITask = new ToggleRESTAPI(remoteNode, false);
                TasksQueue.instance().performSyncTask(toggleRESTAPITask);

                if (toggleRESTAPITask.getState() != TaskState.SUCCESSFUL) {
                    throw new ExperimentException("Enable REST request was not successful for node: " + slaveNode.getName());
                }
            }
        }

        private void addContexts() throws ContextException {

            addContext(cms, experiment, "data_replication_1");
        }

        protected void writePolicyCheckStats() {

            for(Map.Entry<IGUID, Deque<Pair<Long, ArrayList<Integer> > > > entry : cms.getValidPoliciesOverTime().entrySet()) {
                for(Pair<Long, ArrayList<Integer>> pair:entry.getValue()) {

                    InstrumentFactory.instance().measure(StatsTYPE.checkPolicies, StatsTYPE.no_valid_policies, "---", pair.X(), pair.Y().size());
                }
            }

        }

        protected void disableAllNodes(int intervalInSeconds) throws ExperimentException {

            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.ping, "Toggle REST API", System.nanoTime());
            for(ExperimentConfiguration.Experiment.Node slaveNode : experiment.getNodes()) {
                Node remoteNode = new BasicNode(slaveNode.getSsh().getHost(), 8080);
                ToggleRESTAPI toggleRESTAPITask = new ToggleRESTAPI(remoteNode, true);
                TasksQueue.instance().performSyncTask(toggleRESTAPITask);

                if (toggleRESTAPITask.getState() != TaskState.SUCCESSFUL) {
                    throw new ExperimentException("Disable REST request was not successful");
                }

                rest_a_bit(intervalInSeconds * 1000);
            }
        }
    }
}

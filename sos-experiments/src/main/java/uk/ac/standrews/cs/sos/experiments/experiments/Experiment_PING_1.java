package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.node.BasicNode;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.PingNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Node;

/**
 * PING with not payload
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PING_1 extends BaseExperiment implements Experiment {

    public Experiment_PING_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_PING_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_PING_1();
    }

    private class ExperimentUnit_PING_1 implements ExperimentUnit {

        private Node nodeToPing;

        @Override
        public void setup() {

            ExperimentConfiguration.Experiment.Node slaveNode = experiment.getNodes().iterator().next();
            nodeToPing = new BasicNode(slaveNode.getSsh().getHost(), 8080);
        }

        @Override
        public void run() throws ExperimentException {

            for(int i = 0; i < 100; i++) {
                PingNode pingNode = new PingNode(nodeToPing, "", false);
                TasksQueue.instance().performSyncTask(pingNode);

                if (pingNode.getState() != TaskState.SUCCESSFUL) {
                    throw new ExperimentException("Ping request was not successful");
                }

                InstrumentFactory.instance().measure(StatsTYPE.ping, StatsTYPE.none, "No data sent", pingNode.getLatency());
            }
        }

    }

}

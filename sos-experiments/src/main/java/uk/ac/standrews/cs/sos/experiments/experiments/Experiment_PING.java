package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.node.BasicNode;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.PingNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PING extends BaseExperiment implements Experiment {

    public Experiment_PING(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_PING(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_PING();
    }

    private class ExperimentUnit_PING implements ExperimentUnit {

        private Node nodeToPing;

        @Override
        public void setup() {

            ExperimentConfiguration.Experiment.Node slaveNode = experiment.getNodes().iterator().next();
            nodeToPing = new BasicNode(slaveNode.getSsh().getHost(), 8080);
        }

        @Override
        public void run() {

            for(int i = 0; i < 10; i++) {
                PingNode pingNode = new PingNode(nodeToPing, "HELLO WORLD " + i);

                long start = System.nanoTime();
                TasksQueue.instance().performSyncTask(pingNode);
                long duration = System.nanoTime() - start;
                // TODO - change size of payload?
                InstrumentFactory.instance().measure(StatsTYPE.ping, StatsTYPE.none, "amount of data sent", duration);
            }
        }

    }

}

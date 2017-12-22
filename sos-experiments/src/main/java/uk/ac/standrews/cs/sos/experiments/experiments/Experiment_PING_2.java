package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.experiments.third_party.RandomInputStream;
import uk.ac.standrews.cs.sos.impl.node.BasicNode;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.Payload;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.IOException;
import java.io.InputStream;

/**
 * PING with payload
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PING_2 extends BaseExperiment implements Experiment {

    public Experiment_PING_2(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_PING_2(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_PING_2();
    }

    private class ExperimentUnit_PING_2 implements ExperimentUnit {

        private Node nodeToPing;

        @Override
        public void setup() {

            ExperimentConfiguration.Experiment.Node slaveNode = experiment.getNodes().iterator().next();
            nodeToPing = new BasicNode(slaveNode.getSsh().getHost(), 8080);
        }

        @Override
        public void run() throws ExperimentException {

            int payloadSize[] = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 5000000, 10000000, 20000000, 30000000, 40000000, 50000000, 60000000, 70000000, 80000000, 90000000, 100000000};

            for(int i = 0; i < 25; i++) {
                for (int aPayloadSize : payloadSize) {

                    try (InputStream data = new RandomInputStream(aPayloadSize)) {

                        Payload payload = new Payload(nodeToPing, data);
                        TasksQueue.instance().performSyncTask(payload);
                        InstrumentFactory.instance().measure(StatsTYPE.ping, StatsTYPE.none, "amount of data sent", payload.getLatency());

                    } catch (IOException e) {
                        throw new ExperimentException();
                    }

                }
            }

        }

    }

}

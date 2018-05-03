package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.node.BasicNode;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.Payload_JSON;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

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

            String datasetPath = experiment.getExperimentNode().getDatasetPath();
            File folder = new File(datasetPath);
            for(File file:Objects.requireNonNull(folder.listFiles())) {

                long payloadSize = file.length();

                try (FileInputStream fileInputStream = new FileInputStream(file)) {

                    System.out.println("File: " + file.getName() + " payload size: " + payloadSize);
                    // Payload payload = new Payload(nodeToPing, fileInputStream, false);
                    Payload_JSON payload = new Payload_JSON(nodeToPing, fileInputStream, false);
                    TasksQueue.instance().performSyncTask(payload);

                    // FIXME - failing with exception: Unable to make HTTP request: java.net.SocketException: Broken pipe (Write failed)
                    if (payload.getState() != TaskState.SUCCESSFUL) {
                        System.out.println("Unsuccessful");
                    } else {
                        InstrumentFactory.instance().measure(StatsTYPE.ping, StatsTYPE.none, Long.toString(payloadSize), payload.getLatency());
                    }

                    Thread.sleep(500);
                } catch (IOException | InterruptedException e) {
                    throw new ExperimentException();
                }

            }

        }

    }

}

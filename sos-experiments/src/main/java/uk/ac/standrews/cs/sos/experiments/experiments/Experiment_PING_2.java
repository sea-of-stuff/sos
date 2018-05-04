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
import java.util.*;

/**
 * PING with payload
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PING_2 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    public Experiment_PING_2(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_PING_2(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        File[] subsets = new File(experiment.getExperimentNode().getDatasetPath()).listFiles();
        assert(subsets != null);
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (File subset : subsets) {
                units.add(new ExperimentUnit_PING_2(subset));
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

        File[] subsets = new File(experiment.getExperimentNode().getDatasetPath()).listFiles();
        assert(subsets != null);
        return experiment.getSetup().getIterations() * subsets.length;
    }

    private class ExperimentUnit_PING_2 implements ExperimentUnit {

        private File dataset;
        private Node nodeToPing;

        public ExperimentUnit_PING_2(File dataset) {
            this.dataset = dataset;
        }

        @Override
        public void setup() {

            ExperimentConfiguration.Experiment.Node slaveNode = experiment.getNodes().iterator().next();
            nodeToPing = new BasicNode(slaveNode.getSsh().getHost(), 8080);
        }

        @Override
        public void run() throws ExperimentException {

            System.out.println("Processing subset: " + dataset.getAbsolutePath());
            for(File file:Objects.requireNonNull(dataset.listFiles())) {

                long payloadSize = file.length();
                try (FileInputStream fileInputStream = new FileInputStream(file)) {

                    // System.out.println("File: " + file.getName() + " payload size: " + payloadSize);
                    // Payload payload = new Payload(nodeToPing, fileInputStream, false); // NOTE - Payload via inputstream won't work
                    Payload_JSON payload = new Payload_JSON(nodeToPing, fileInputStream, false);
                    TasksQueue.instance().performSyncTask(payload);

                    if (payload.getState() != TaskState.SUCCESSFUL) {
                        System.out.println("Unsuccessful");
                    } else {
                        InstrumentFactory.instance().measure(StatsTYPE.ping, StatsTYPE.none, Long.toString(payloadSize), payload.getLatency());
                    }

                } catch (IOException e) {
                    System.out.println("Exception thrown :(");
                    throw new ExperimentException();
                }

            }

        }

    }

}

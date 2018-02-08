package uk.ac.standrews.cs.sos.experiments.experiments;

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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Simulate failure by making remote nodes unresponsive to REST calls (except a special one that is needed to wake the node up again)
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_Failure_4 extends Experiment_Failure implements Experiment {

    public Experiment_Failure_4(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            units.add(new ExperimentUnit_Failure_4(i));
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    private class ExperimentUnit_Failure_4 extends ExperimentUnit_Failure {

        ExperimentUnit_Failure_4(int index) {
            super(index);
        }

        @Override
        public void run() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.experiment, "Experiment Unit Index", System.nanoTime(), index);

            System.out.println("Running Policies");
            cms.runPolicies();
            cms.runCheckPolicies();

            // Disable REST API on remote nodes at regular interval
            Runnable task = () -> {
                try {
                    disableAllNodes(10);
                } catch (ExperimentException e) {
                    e.printStackTrace();
                }
            };

            Thread thread = new Thread(task);
            thread.start();


            // The check policy thread runs every 30 seconds according to the master experiment node configuration (see sif_12.json).
            rest_a_bit(90 * 1000); // 1.5 minutes

            writePolicyCheckStats();
        }

        private void disableAllNodes(int intervalInSeconds) throws ExperimentException {

            for(ExperimentConfiguration.Experiment.Node slaveNode : experiment.getNodes()) {
                Node remoteNode = new BasicNode(slaveNode.getSsh().getHost(), 8080);
                ToggleRESTAPI toggleRESTAPITask = new ToggleRESTAPI(remoteNode, true);
                TasksQueue.instance().performSyncTask(toggleRESTAPITask);
                InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.ping, "Toggle REST API", System.nanoTime());

                if (toggleRESTAPITask.getState() != TaskState.SUCCESSFUL) {
                    throw new ExperimentException("Disable REST request was not successful");
                }

                rest_a_bit(intervalInSeconds * 1000);
            }
        }

    }
}

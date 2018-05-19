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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_Failure_8 extends Experiment_Failure implements Experiment {

    public Experiment_Failure_8(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            units.add(new ExperimentUnit_Failure_8(i));
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    private class ExperimentUnit_Failure_8 extends ExperimentUnit_Failure {

        ExperimentUnit_Failure_8(int index) {
            super(index);
        }

        @Override
        public void run() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.experiment, "Experiment Unit Index", System.nanoTime(), index);
            long start = System.currentTimeMillis();

            disableAllExceptFirstSlaveNode();

            System.out.println("Running Policies");
            cms.runPolicies();
            cms.runCheckPolicies();

            System.out.println("A -- " + (System.currentTimeMillis() - start) / 1000.0 + " s");

            rest_a_bit(15 * 1000);

            ExperimentConfiguration.Experiment.Node firstSlaveNode = experiment.getNodes().get(0);
            ExperimentConfiguration.Experiment.Node secondSlaveNode = experiment.getNodes().get(1);
            ExperimentConfiguration.Experiment.Node thirdSlaveNode = experiment.getNodes().get(2);

            System.out.println("B -- " + (System.currentTimeMillis() - start) / 1000.0 + " s");
            disableNode(firstSlaveNode);

            reEnableNode(firstSlaveNode, 45);
            reEnableNode(secondSlaveNode, 0);
            System.out.println("C -- " + (System.currentTimeMillis() - start) / 1000.0 + " s");

            rest_a_bit(35 * 1000);
            disableNode(firstSlaveNode);
            System.out.println("D -- " + (System.currentTimeMillis() - start) / 1000.0 + " s");

            reEnableNode(thirdSlaveNode, 10);
            System.out.println("E -- " + (System.currentTimeMillis() - start) / 1000.0 + " s");

            rest_a_bit(90 * 1000); // 1 minute

            writePolicyCheckStats();
        }

        private void disableAllExceptFirstSlaveNode() throws ExperimentException {

            try {
                // Disable all slave nodes, except the first one
                Runnable task = () -> {
                    try {
                        changeRESTAPIonAllNodes(0, true);
                    } catch (ExperimentException e) {
                        e.printStackTrace();
                    }
                };

                Thread thread = new Thread(task);
                thread.start();
                thread.join();

                ExperimentConfiguration.Experiment.Node slaveNode = experiment.getNodes().iterator().next();
                reEnableNode(slaveNode, 0);

            } catch (InterruptedException e) {
                throw new ExperimentException(e);
            }
        }

        private void disableNode(ExperimentConfiguration.Experiment.Node slaveNode) throws ExperimentException {

            Node remoteNode = new BasicNode(slaveNode.getSsh().getHost(), 8080);
            ToggleRESTAPI toggleRESTAPITask = new ToggleRESTAPI(remoteNode, true);
            TasksQueue.instance().performSyncTask(toggleRESTAPITask);

            if (toggleRESTAPITask.getState() != TaskState.SUCCESSFUL) {
                throw new ExperimentException("Disable REST request was not successful");
            }
        }

        private void reEnableNode(ExperimentConfiguration.Experiment.Node slaveNode, int delay) throws ExperimentException {

            try {

                // Re-Enable node after 60 seconds
                Runnable task = () -> {
                    try {
                        rest_a_bit("ReEnabling nodes", delay * 1000);

                        Node remoteNode = new BasicNode(slaveNode.getSsh().getHost(), 8080);
                        ToggleRESTAPI toggleRESTAPITask = new ToggleRESTAPI(remoteNode, false);
                        TasksQueue.instance().performSyncTask(toggleRESTAPITask);

                        if (toggleRESTAPITask.getState() != TaskState.SUCCESSFUL) {
                            throw new ExperimentException("Re-Enable REST request was not successful");
                        }
                    } catch (ExperimentException e) {
                        e.printStackTrace();
                    }
                };

                Thread thread = new Thread(task);
                thread.start();

                thread.join();

            } catch (InterruptedException e) {
                throw new ExperimentException(e);
            }
        }

    }
}

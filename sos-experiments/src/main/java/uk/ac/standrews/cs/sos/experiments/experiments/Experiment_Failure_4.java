package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

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

            rest_a_bit(10 * 1000);

            // Disable REST API on remote nodes at regular interval
            Runnable task = () -> {
                try {
                    changeRESTAPIonAllNodes(15, true);
                } catch (ExperimentException e) {
                    e.printStackTrace();
                }
            };

            Thread thread = new Thread(task);
            thread.start();

            // The check policy thread runs every 30 seconds according to the master experiment node configuration (see sif_12.json).
            rest_a_bit(240 * 1000); // 4 minutes

            writePolicyCheckStats();
        }

    }
}

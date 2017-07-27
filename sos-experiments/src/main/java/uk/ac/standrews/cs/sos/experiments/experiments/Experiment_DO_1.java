package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Investigate the context performance as the cardinality of its domain changes
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_DO_1 extends BaseExperiment implements Experiment {

    public Experiment_DO_1(ExperimentConfiguration experimentConfiguration) {
        super(experimentConfiguration);

        // Prepare the experiments to be run
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
                units.add(new ExperimentUnit_DO_1());
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations();
    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_DO_1 experiment_pr_1 = new Experiment_DO_1(experimentConfiguration);
        experiment_pr_1.process();
    }

    private class ExperimentUnit_DO_1 implements ExperimentUnit {

        ExperimentUnit_DO_1() {

        }

        @Override
        public void setup() throws ExperimentException {
        }

        @Override
        public void run() {
        }

    }

}

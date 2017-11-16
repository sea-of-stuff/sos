package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_CO_3 extends BaseExperiment implements Experiment {

    public Experiment_CO_3(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_CO_3();
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations();
    }

    private class ExperimentUnit_CO_3 implements ExperimentUnit {

        ExperimentUnit_CO_3() {

        }

        @Override
        public void setup() throws ExperimentException {
            // add data
            // add context
        }

        @Override
        public void run() {
        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "co_3") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_CO_3 experiment_co_3 = new Experiment_CO_3(experimentConfiguration);
        experiment_co_3.process();
    }


}

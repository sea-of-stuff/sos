package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.io.File;
import java.lang.reflect.Constructor;

import static uk.ac.standrews.cs.sos.experiments.experiments.BaseExperiment.CONFIGURATION_FOLDER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentManager {

    /**
     * @param experimentConfiguration configuration for the experiment
     * @param experiment the name of the class for the experiment, the package name will be added by this method
     * @throws ExperimentException if the experiment could not be started correctly or the class was not found
     */
    public static void runExperiment(ExperimentConfiguration experimentConfiguration, String experiment) throws ExperimentException {

        try {
            Class myClass = Class.forName("uk.ac.standrews.cs.sos.experiments.experiments." + experiment);
            Constructor constructor = myClass.getConstructor(ExperimentConfiguration.class);
            Experiment instanceOfMyClass = (Experiment) constructor.newInstance(experimentConfiguration);

            instanceOfMyClass.process();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ExperimentException();
        }
    }

    public static void main(String[] args) throws Exception {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ExperimentManager.runExperiment(experimentConfiguration, "Experiment_PR_1");
    }
}

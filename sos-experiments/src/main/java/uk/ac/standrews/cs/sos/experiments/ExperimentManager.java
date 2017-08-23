package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.io.File;
import java.lang.reflect.Constructor;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentManager {

    /**
     * @param experimentConfiguration configuration for the experiment
     * @throws ExperimentException if the experiment could not be started correctly or the class was not found
     */
    public static void runExperiment(ExperimentConfiguration experimentConfiguration) throws ExperimentException {

        try {
            Class<?> myClass = Class.forName("uk.ac.standrews.cs.sos.experiments.experiments." + experimentConfiguration.getExperimentObj().getExperimentClass());
            Class<?>[] params = new Class[] {ExperimentConfiguration.class};
            Constructor<?> constructor = myClass.getConstructor(params);
            Experiment instanceOfMyClass = (Experiment) constructor.newInstance(experimentConfiguration);

            instanceOfMyClass.process();

        } catch (Exception e) {
            throw new ExperimentException("Unable to instantiate experiment", e);
        }
    }

    public static void main(String[] args) throws Exception {

        // File experimentConfigurationFile = new File("sos-experiments/src/main/resources/experiments/scale_1/configuration/configuration.json");
        File experimentConfigurationFile = new File("experiment.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        // Run the experiment only. We assume that the distribution for this experiment has already been done.
        ExperimentManager.runExperiment(experimentConfiguration);
    }
}

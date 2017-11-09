package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.io.File;
import java.lang.reflect.Constructor;

import static uk.ac.standrews.cs.sos.experiments.distribution.SOSDistribution.REMOTE_SOS_EXPERIMENT_CONFIGURATION_PATH;

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

    public static void runExperiment(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {

        try {
            Class<?> myClass = Class.forName("uk.ac.standrews.cs.sos.experiments.experiments." + experimentConfiguration.getExperimentObj().getExperimentClass());
            Class<?>[] params = new Class[] {ExperimentConfiguration.class, String.class};
            Constructor<?> constructor = myClass.getConstructor(params);
            Experiment instanceOfMyClass = (Experiment) constructor.newInstance(experimentConfiguration, outputFilename);

            instanceOfMyClass.process();

        } catch (Exception e) {
            throw new ExperimentException("Unable to instantiate experiment", e);
        }
    }

    /**
     *
     * @param args
     *  args[0] - optional - name of output files for experiment
     * @throws Exception if the experiment node could not be started
     */
    public static void main(String[] args) throws Exception {

        File experimentConfigurationFile = new File(REMOTE_SOS_EXPERIMENT_CONFIGURATION_PATH);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        if (args.length == 0) {
            // Run the experiment only. We assume that the distribution for this experiment has already been done.
            ExperimentManager.runExperiment(experimentConfiguration);
        } else {
            ExperimentManager.runExperiment(experimentConfiguration, args[0]);
        }
    }
}

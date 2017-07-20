package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.lang.reflect.Constructor;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentManager {

    /**
     *
     * @param experiment the name of the class, the package name will be added by this method
     * @throws Exception
     */
    public static void runExperiment(String experiment) throws ExperimentException {

        try {
            Class myClass = Class.forName("uk.ac.standrews.cs.sos.experiments.experiments." + experiment);
            Constructor constructor = myClass.getConstructor();
            Experiment instanceOfMyClass = (Experiment) constructor.newInstance();

            instanceOfMyClass.run();
        } catch (Exception e) {
            throw new ExperimentException();
        }
    }

    public static void main(String[] args) throws Exception {

        ExperimentManager.runExperiment("Experiment_PR_1");
    }
}

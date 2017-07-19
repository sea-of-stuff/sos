package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.distribution.SOSDistribution;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseExperiment implements Experiment {

    public static final String CONFIGURATION_FOLDER = "sos-experiments/src/main/resources/configurations/";

    protected ExperimentConfiguration experimentConfiguration;
    private long start, end, timeToFinish;

    public BaseExperiment(String experimentConfigurationPath) throws ConfigurationException {
        File experimentConfigurationFile = new File(experimentConfigurationPath);
        experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);
    }

    public void setup() throws Exception {
        System.out.println("SETTING UP EXPERIMENT: " + experimentConfiguration.getExperimentName());

        System.out.println("Distributing the SOS app to nodes in the network");
        new SOSDistribution().distribute(experimentConfiguration);

        System.out.println("Finished to distribute the SOS app to nodes in the network");
    }

    @Override
    public void start() {
        start = System.nanoTime();
    }

    @Override
    public void finish() {
        end = System.nanoTime();
    }

    @Override
    public void collectStats() {
        timeToFinish = end - start;
        System.out.println("Experiment run in " + timeToFinish/1000000000.0 + " seconds");
    }
}

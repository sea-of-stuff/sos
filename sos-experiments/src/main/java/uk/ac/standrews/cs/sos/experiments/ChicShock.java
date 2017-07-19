package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.distribution.NetworkException;
import uk.ac.standrews.cs.sos.experiments.distribution.SOSDistribution;

import java.io.File;

/**
 * ChicShock does not mean anything. This is simply a word I invented and I like, so here it is.
 * Boom.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ChicShock {

    private File experimentConfigurationFile;
    private ExperimentConfiguration experimentConfiguration;

    public static void main(String[] args) throws ConfigurationException, NetworkException, InterruptedException {

        ChicShock chicShock = new ChicShock(args[0]);

        chicShock.chic();
        chicShock.chicExperiment("Experiment_X_1");

        chicShock.shock();
        chicShock.shockExperiment();

        // RUN Experiment
        // Stop all nodes when experiment is finished


        chicShock.unChick();
    }

    public ChicShock(String configurationFilePath) throws ConfigurationException {
        experimentConfigurationFile = new File(configurationFilePath);
        experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);
    }

    // Just distribute the file. It won't do anything else
    public void chic() throws NetworkException, InterruptedException {

        System.out.println("SETTING UP EXPERIMENT: " + experimentConfiguration.getExperimentObj().getName());

        System.out.println("Distributing the SOS app to nodes in the network");
        SOSDistribution.distribute(experimentConfiguration);
        System.out.println("Finished to distribute the SOS app to nodes in the network");
    }

    /**
     * Distribute the SOS to the node that will run the experiment
     */
    public void chicExperiment(String experiment) {

        // This might also be distributed locally
    }

    public void shock() throws NetworkException, InterruptedException {

        System.out.println("Starting the remote SOS Nodes");
        SOSDistribution.startAllApplications(experimentConfiguration);
    }

    /**
     * Run the experiment from the experiment node
     */
    public void shockExperiment() {

        // This might also be run locally
    }

    public void unChick() throws NetworkException, InterruptedException {

        System.out.println("Stopping the remote SOS Nodes");
        SOSDistribution.stopAllApplications(experimentConfiguration);
    }
}

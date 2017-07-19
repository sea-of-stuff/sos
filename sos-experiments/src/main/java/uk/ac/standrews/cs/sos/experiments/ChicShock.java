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

    public static void main(String[] args) throws Exception { // FIXME - do not use a generic exception here.

        ChicShock chicShock = new ChicShock(args[0]);

        chicShock.chic();
        chicShock.chicExperiment();

        chicShock.shock();
        chicShock.shockExperiment("Experiment_X_1");

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
    public void chicExperiment() throws Exception {
        // TODO
    }

    public void shock() throws NetworkException, InterruptedException {

        System.out.println("Starting the remote SOS Nodes");
        SOSDistribution.startAllApplications(experimentConfiguration);
    }

    /**
     * Run the experiment from the experiment node.
     * This method should return only when the experiment is finished.
     */
    public void shockExperiment(String experiment) throws Exception {

        // This might also be distributed locally
        boolean isLocal = true;
        if (isLocal) {
            ExperimentManager.runExperiment(experiment);
        } else {
            SOSDistribution.runExperiment(experimentConfiguration);
        }

        // Instruct a remote node to run the experiment

        // Wait for a response back from that node and then return
    }

    public void unChick() throws NetworkException, InterruptedException {

        System.out.println("Stopping the remote SOS Nodes");
        SOSDistribution.stopAllApplications(experimentConfiguration);
    }
}

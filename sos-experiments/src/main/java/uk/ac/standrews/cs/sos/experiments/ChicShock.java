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

    public static void main(String[] args) throws ChicShockException, ConfigurationException {

        ChicShock chicShock = new ChicShock(args[0]);

        chicShock.chic();
        chicShock.chicExperiment();

        chicShock.shock();
        chicShock.shockExperiment("Experiment_X_1");

        // RUN Experiment

        // Stop all nodes when experiment is finished
        chicShock.unShock();
    }

    public ChicShock(String configurationFilePath) throws ConfigurationException {
        experimentConfigurationFile = new File(configurationFilePath);
        experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);
    }

    // Just distribute the file. It won't do anything else
    public void chic() throws ChicShockException {

        System.out.println("SETTING UP EXPERIMENT: " + experimentConfiguration.getExperimentObj().getName());

        System.out.println("Distributing the SOS app to nodes in the network");
        try {
            SOSDistribution.distribute(experimentConfiguration);
        } catch (InterruptedException | NetworkException e) {
            throw new ChicShockException();
        }
        System.out.println("Finished to distribute the SOS app to nodes in the network");
    }

    /**
     * Distribute the SOS to the node that will run the experiment.
     * The configuration for the node is also distributed.
     */
    public void chicExperiment() throws ChicShockException {
        // TODO
    }

    public void shock() throws ChicShockException {

        System.out.println("Starting the remote SOS Nodes");
        try {
            SOSDistribution.startAllApplications(experimentConfiguration);
        } catch (InterruptedException | NetworkException e) {
            throw new ChicShockException();
        }
    }

    /**
     * Run the experiment from the experiment node.
     * This method should return only when the experiment is finished.
     */
    public void shockExperiment(String experiment) throws ChicShockException {

        try {
            boolean isLocal = true;
            if (isLocal) {
                ExperimentManager.runExperiment(experiment);
            } else {
                SOSDistribution.runExperiment(experimentConfiguration);
            }
        } catch (ExperimentException e) {
            throw new ChicShockException();
        }

        // TODO
        // Wait for a response back from that node and then return
    }

    public void unShock() throws ChicShockException {

        System.out.println("Stopping the remote SOS Nodes");
        try {
            SOSDistribution.stopAllApplications(experimentConfiguration);
        } catch (InterruptedException | NetworkException e) {
            throw new ChicShockException();
        }
    }

    public void unChic() {
        // TODO - remove the files from the remote nodes
    }
}

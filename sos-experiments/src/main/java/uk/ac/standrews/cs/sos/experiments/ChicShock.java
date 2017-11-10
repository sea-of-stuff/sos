package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.distribution.NetworkException;
import uk.ac.standrews.cs.sos.experiments.distribution.SOSDistribution;
import uk.ac.standrews.cs.sos.experiments.exceptions.ChicShockException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.io.File;
import java.io.IOException;

/**
 * ChicShock does not mean anything. This is simply a word I invented and I like, so here it is.
 * Boom.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ChicShock {

    private ExperimentConfiguration experimentConfiguration;

    public static void main(String[] args) throws ChicShockException, ConfigurationException {

        File experimentConfigurationFile = new File(args[0]);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);

        // DISTRIBUTION PHASE
        chicShock.chic();
        chicShock.chicExperiment();

        // RUNNING PHASE
        chicShock.shock();
        chicShock.shockExperiment(null);

        // STOP PHASE
        chicShock.unShock();
        chicShock.unChic();
    }

    public ChicShock(ExperimentConfiguration experimentConfiguration) throws ConfigurationException {
        this.experimentConfiguration = experimentConfiguration;
    }

    // Just distribute the file. It won't do anything else
    public void chic() throws ChicShockException {
        System.out.println("SETTING UP EXPERIMENT: " + experimentConfiguration.getExperimentObj().getName());
        System.out.println("Description: " + experimentConfiguration.getExperimentObj().getDescription());

        try {
            SOSDistribution.distribute(experimentConfiguration);
        } catch (InterruptedException | NetworkException e) {
            throw new ChicShockException();
        }
    }

    /**
     * Distribute the SOS to the node that will process the experiment.
     * The configuration for the node is also distributed.
     */
    public void chicExperiment() throws ChicShockException {

        try {
            if (experimentConfiguration.getExperimentObj().getExperimentNode().isRemote()) {
                SOSDistribution.distributeToExperimentNode(experimentConfiguration);
            }
        } catch (NetworkException | IOException e) {
            throw new ChicShockException();
        }
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
    public void shockExperiment(String outputfile) throws ChicShockException {
        System.out.println("Starting the experiment remotely");

        if (outputfile == null || outputfile.isEmpty()) {

            try {
                boolean isRemote = experimentConfiguration.getExperimentObj().getExperimentNode().isRemote();
                if (isRemote) {
                    SOSDistribution.runExperiment(experimentConfiguration, "");
                } else {
                    ExperimentManager.runExperiment(experimentConfiguration);
                }
            } catch (ExperimentException | NetworkException e) {
                throw new ChicShockException();
            }
        } else {

            try {
                boolean isRemote = experimentConfiguration.getExperimentObj().getExperimentNode().isRemote();
                if (isRemote) {
                    SOSDistribution.runExperiment(experimentConfiguration, outputfile);
                } else {
                    ExperimentManager.runExperiment(experimentConfiguration, outputfile);
                }
            } catch (ExperimentException | NetworkException e) {
                throw new ChicShockException();
            }
        }
    }

    public void unShock() throws ChicShockException {
        System.out.println("Stopping the remote SOS Nodes");

        try {
            SOSDistribution.stopAllApplications(experimentConfiguration);
        } catch (InterruptedException | NetworkException e) {
            throw new ChicShockException();
        }
    }

    public void unShock(String nodeName) throws ChicShockException {
        System.out.println("Stopping the remote SOS Node named: " + nodeName);

        try {
            SOSDistribution.stopNode(experimentConfiguration, nodeName);
        } catch (InterruptedException | NetworkException e) {
            throw new ChicShockException();
        }
    }

    public void unShockExperiment() throws ChicShockException {
        System.out.println("Stopping the remote experiment SOS Nodes");

        try {
            SOSDistribution.stopExperiment(experimentConfiguration);
        } catch (InterruptedException | NetworkException e) {
            throw new ChicShockException();
        }
    }

    public void unChic() throws ChicShockException {
        System.out.println("Removing the SOS files from the remote nodes");

        try {
            SOSDistribution.undoDistribution(experimentConfiguration);
        } catch (NetworkException e) {
            throw new ChicShockException();
        }

    }

    public void unChicExperiment() throws ChicShockException {
        System.out.println("Removing the SOS files from the remote nodes");

        try {
            SOSDistribution.undoDistributionToExperimentNode(experimentConfiguration);
        } catch (NetworkException e) {
            throw new ChicShockException();
        }

    }
}

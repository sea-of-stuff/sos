package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.distribution.NetworkException;
import uk.ac.standrews.cs.sos.experiments.distribution.SOSDistribution;
import uk.ac.standrews.cs.sos.experiments.exceptions.ChicShockException;

import java.io.File;
import java.util.Scanner;

import static uk.ac.standrews.cs.sos.instrument.impl.BackgroundInstrument.OS_FILE;
import static uk.ac.standrews.cs.sos.instrument.impl.BasicInstrument.DATASET_FILES;
import static uk.ac.standrews.cs.sos.instrument.impl.BasicInstrument.DATASET_SUMMARY;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentalFramework {

    private static final String RUN_EXPERIMENT = "run";
    private static final String CRON_EXPERIMENT = "cron";
    private static final String CHECK_NODE = "check";
    private static final String STOP_NODE = "stop";
    private static final String STOP_ALL_NODES = "stopall";
    private static final String STATS_NODE = "stats";
    private static final String CLEAN_NODE = "clean";

    private static final String CONFIGURATION_FOLDER = "sos-experiments/src/main/resources/experiments/";

    private static Scanner in;
    /*

       Options:
       - run configuration_path (e.g. run pr_1/configuration-hogun.json)
       - cron configuration_path cronfile

       - check node_config (e.g. check pr_1/hogun_1.json) - prints a short summary of this node, such as IT IS RUNNING, IT CRASHED, etc
       - stop configuration_path node_name (e.g. stop pr_1/hogun_1.json)
       - stats node_config dest_path (e.g. stats pr_1/hogun_1.json experiments/output) - downloads all stats from the node

        */
    public static void main(String[] args) throws InterruptedException, ConfigurationException, ChicShockException, NetworkException {

        System.out.println("Options: run, cron, stop, stopall, check, stats, clean");
        in = new Scanner(System.in);
        String option = in.nextLine();

        System.out.println("Enter experiment configuration path relative to " + CONFIGURATION_FOLDER);
        System.out.println("Examples:");
        System.out.println("\t pr_1/configuration/configuration.json");
        System.out.println("\t pr_1/configuration/configuration-hogun.json");
        System.out.println("\t po_a_1/configuration/configuration-hogun.json");
        System.out.println("\t po_a_3/configuration/configuration-hogun.json");
        System.out.println("\t po_c_1/configuration/configuration-hogun.json");
        System.out.println("\t po_c_3/configuration/configuration-hogun.json");
        switch(option.toLowerCase()) {

            case RUN_EXPERIMENT:
                runExperiment();
                break;

            case STOP_NODE:
                stopNode();
                break;

            case STOP_ALL_NODES:
                stopAllNodes();
                break;

            case STATS_NODE:
                getStats();
                break;

            case CHECK_NODE:
            case CRON_EXPERIMENT:
            case CLEAN_NODE:
                break;
        }

    }

    private static void runExperiment() throws ConfigurationException, ChicShockException, InterruptedException {

        String configurationPath = in.nextLine();
        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configurationPath);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        System.out.println("Enter base name of stats to collect. Leave empty for standard naming.");
        String statsBaseName = in.nextLine();

        // Distribute sos app to nodes
        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.chic();
        chicShock.chicExperiment();

        // Make sure that no processes are running and then run the nodes and the experiment
        chicShock.unShock();
        Thread.sleep(2000); // Wait a bit before stopping the experiment node (if any process is running)
        chicShock.unShockExperiment();
        Thread.sleep(2000); // Wait a bit before starting the nodes for the experiment
        chicShock.shock();
        Thread.sleep(2000); // Wait a bit before starting the experiment
        chicShock.shockExperiment(statsBaseName);
    }

    private static void stopNode() throws ConfigurationException, ChicShockException {

        String configurationPath = in.nextLine();
        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configurationPath);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        System.out.println("Enter name of node to stop. Options:");
        for(ExperimentConfiguration.Experiment.Node node:experimentConfiguration.getExperimentObj().getNodes()) {
            System.out.println("\t\t" + node.getName());
        }
        ExperimentConfiguration.Experiment.Node node = experimentConfiguration.getExperimentObj().getExperimentNode();
        System.out.println("\t\t" + node.getName());

        String nodeName = in.nextLine();

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.unShock(nodeName);
    }

    private static void stopAllNodes() throws ConfigurationException, ChicShockException, InterruptedException {

        String configurationPath = in.nextLine();
        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configurationPath);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.unShock();
        Thread.sleep(2000); // Wait a bit before stopping the experiment node
        chicShock.unShockExperiment();
    }

    private static void getStats() throws ConfigurationException, NetworkException {

        String configurationPath = in.nextLine();
        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configurationPath);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        System.out.println("Enter base name of stats to collect");
        String statsBaseName = in.nextLine();

        SOSDistribution.getFileFromExperimentNode(experimentConfiguration, "experiments/output/" + statsBaseName + ".tsv", "experiments/remote/" + statsBaseName + ".tsv");
        SOSDistribution.getFileFromExperimentNode(experimentConfiguration, "experiments/output/" + statsBaseName + OS_FILE, "experiments/remote/" + statsBaseName + OS_FILE);
        SOSDistribution.getFileFromExperimentNode(experimentConfiguration, "experiments/output/" + statsBaseName + DATASET_SUMMARY, "experiments/remote/" + statsBaseName + DATASET_SUMMARY);
        SOSDistribution.getFileFromExperimentNode(experimentConfiguration, "experiments/output/" + statsBaseName + DATASET_FILES, "experiments/remote/" + statsBaseName + DATASET_FILES);

        System.out.println("Stat files collected at experiments/remote/" + statsBaseName + "*");
    }
}

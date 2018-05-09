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
    private static final String CLEAN_ALL_NODES = "cleanall";

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

        System.out.println("Options: run, cron (N/A), stop, stopall, check (N/A), stats, clean, cleanall");
        in = new Scanner(System.in);
        String option = in.nextLine();

        System.out.println("Enter experiment configuration path relative to " + CONFIGURATION_FOLDER);
        System.out.println("Configuration options (enter number):");
        String[] configurations = new String[] {
                "pr_1/configuration/configuration.json",
                "pr_1/configuration/configuration-hogun.json",
                "pr_1/configuration/configuration-hogun-mixed-content.json",
                "po_a_1/configuration/configuration-hogun.json",
                "po_a_3/configuration/configuration-hogun.json",
                "po_c_1/configuration/configuration-hogun.json",
                "po_c_3/configuration/configuration-hogun.json",
                "co_a_1/configuration/configuration-hogun.json",
                "co_a_1/configuration/configuration-sif.json",
                "co_a_2/configuration/configuration-hogun.json",
                "co_c_1/configuration/configuration-hogun.json",
                "co_c_2/configuration/configuration-hogun.json",
                "do_1/configuration/configuration-sif.json",
                "do_1/configuration/configuration-sif-alternative.json",
                "do_1/configuration/configuration-hogun.json",
                "do_2/configuration/configuration-sif.json",
                "do_2/configuration/configuration-sif-alternative.json",
                "do_3/configuration/configuration-sif.json",
                "ping_1/configuration/configuration-sif.json",
                "ping_2/configuration/configuration-sif.json",
                "nb_1/configuration/configuration-sif.json",
                "repl_1/configuration/configuration-sif.json",
                "repl_1/configuration/configuration-hogun.json",
                "repl_2/configuration/configuration-sif.json",
                "repl_2/configuration/configuration-hogun.json",
                "io_1/configuration/configuration-sif.json",
                "io_2/configuration/configuration.json",
                "io_2/configuration/configuration-sif.json",
                "io_2/configuration/configuration-sif-2.json",
                "guid_2/configuration/configuration-sif.json",
                "failure_1/configuration/configuration-sif.json",
                "failure_2/configuration/configuration-sif.json",
                "failure_3/configuration/configuration-sif.json",
                "failure_4/configuration/configuration-sif.json",
                "failure_5/configuration/configuration-sif.json",
                "failure_6/configuration/configuration-sif.json",
                "failure_7/configuration/configuration-sif.json"
        };
        for(int i = 0; i < configurations.length; i++) {
            System.out.println("\t[" + i + "] " + configurations[i]);
        }
        int configurationIndex = Integer.parseInt(in.nextLine());
        String configuration = configurations[configurationIndex];
        System.out.println("Processing configuration: " + configurationIndex + " --- " + configuration);

        switch(option.toLowerCase()) {

            case RUN_EXPERIMENT:
                runExperiment(configuration);
                break;

            case STOP_NODE:
                stopNode(configuration);
                break;

            case STOP_ALL_NODES:
                stopAllNodes(configuration);
                break;

            case CLEAN_NODE:
                cleanNode(configuration);
                break;

            case CLEAN_ALL_NODES:
                cleanAllNodes(configuration);
                break;

            case STATS_NODE:
                getStats(configuration);
                break;

            case CHECK_NODE:
            case CRON_EXPERIMENT:
                System.err.println("COMMAND NOT IMPLEMENTED YET");
                break;
        }

        System.exit(0);
    }

    private static void runExperiment(String configuration) throws ConfigurationException, ChicShockException, InterruptedException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configuration);
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

        // Run nodes and experiment
        chicShock.shock();

        Thread.sleep(2000); // Wait a bit before starting the experiment
        chicShock.shockExperiment(statsBaseName);

        System.out.println("\n\nRemember that your base name for the stats is: " + statsBaseName);
    }

    private static void stopNode(String configuration) throws ConfigurationException, ChicShockException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configuration);
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

    private static void stopAllNodes(String configuration) throws ConfigurationException, ChicShockException, InterruptedException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configuration);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.unShock();
        Thread.sleep(2000); // Wait a bit before stopping the experiment node
        chicShock.unShockExperiment();
    }

    private static void cleanNode(String configuration) throws ConfigurationException, ChicShockException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configuration);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        System.out.println("Enter name of node to stop. Options:");
        for(ExperimentConfiguration.Experiment.Node node:experimentConfiguration.getExperimentObj().getNodes()) {
            System.out.println("\t\t" + node.getName());
        }
        ExperimentConfiguration.Experiment.Node node = experimentConfiguration.getExperimentObj().getExperimentNode();
        System.out.println("\t\t" + node.getName());

        String nodeName = in.nextLine();

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.unChic(nodeName);
    }

    private static void cleanAllNodes(String configuration) throws ConfigurationException, ChicShockException, InterruptedException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configuration);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.unChic();
        Thread.sleep(2000); // Wait a bit before stopping the experiment node
        chicShock.unChicExperiment();
    }

    private static void getStats(String configuration) throws ConfigurationException, NetworkException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configuration);
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

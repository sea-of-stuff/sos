package uk.ac.standrews.cs.sos.experiments;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ChicShockException;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentalFramework {

    private static final String RUN_EXPERIMENT = "run";
    private static final String CHECK_NODE = "check";
    private static final String STOP_NODE = "stop";
    private static final String STOP_ALL_NODES = "stop_all";
    private static final String STATS_NODE = "stats";
    private static final String CLEAN_NODE = "clean"; // TODO - remove files from remote node

    public static final String CONFIGURATION_FOLDER = "sos-experiments/src/main/resources/experiments/";

    /*

       Options:
       - run configuration_path (e.g. run pr_1/configuration-hogun.json)
           - cronjob

       - check node_config (e.g. check pr_1/hogun_1.json) - prints a short summary of this node, such as IT IS RUNNING, IT CRASHED, etc
       - stop configuration_path node_name (e.g. stop pr_1/hogun_1.json)
       - stats node_config dest_path (e.g. stats pr_1/hogun_1.json experiments/output) - downloads all stats from the node

        */
    public static void main(String[] args) throws ParseException, InterruptedException, ConfigurationException, ChicShockException {
        CommandLine cli = InitCLI(args);

        if (cli.hasOption(RUN_EXPERIMENT)) {
            runExperiment(cli);
        } else if (cli.hasOption(CHECK_NODE)) {
            // TODO
        } else if (cli.hasOption(STOP_NODE)) {
            stopNode(cli);
        } else if (cli.hasOption(STOP_ALL_NODES)) {
            stopAllNodes(cli);
        } else if (cli.hasOption(STATS_NODE)) {
            // TODO
        }
    }

    private static void runExperiment(CommandLine cli) throws ConfigurationException, ChicShockException, InterruptedException {

        String configurationPath = cli.getOptionValue(RUN_EXPERIMENT);
        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configurationPath);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        // Distribute sos app to nodes
        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.chic();
        chicShock.chicExperiment();

        // Run the nodes and the experiment
        chicShock.shock();
        Thread.sleep(2000); // Wait a bit before starting the experiment
        chicShock.shockExperiment();
    }

    private static void stopNode(CommandLine cli) throws ConfigurationException, ChicShockException {

        String[] options = cli.getOptionValues(STOP_NODE);

        String configurationPath = options[0];
        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configurationPath);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        String nodeName = options[1];

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.unShock(nodeName);
    }

    private static void stopAllNodes(CommandLine cli) throws ConfigurationException, ChicShockException, InterruptedException {

        String configurationPath = cli.getOptionValue(RUN_EXPERIMENT);
        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + configurationPath);
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.unShock();
        Thread.sleep(2000); // Wait a bit before stopping the experiment node
        chicShock.unShockExperiment();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CLI SETTINGS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static CommandLine InitCLI(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = CreateOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("posix", options);
        System.out.println("\n===================================================\n\n");

        return parser.parse(options, args);
    }

    private static Options CreateOptions() {
        Options options = new Options();

        options.addOption(Option.builder(RUN_EXPERIMENT)
                .required(false)
                .hasArg()
                .desc("Run experiment")
                .build());

        options.addOption(Option.builder(CHECK_NODE)
                .required(false)
                .hasArgs()
                .desc("Check node")
                .build());

        options.addOption(Option.builder(STOP_NODE)
                .required(false)
                .hasArgs()
                .desc("Stop node")
                .build());

        options.addOption(Option.builder(STOP_ALL_NODES)
                .required(false)
                .desc("Stop all nodes")
                .build());

        options.addOption(Option.builder(STATS_NODE)
                .required(false)
                .hasArgs()
                .desc("Get node stats")
                .build());

        return options;
    }

}

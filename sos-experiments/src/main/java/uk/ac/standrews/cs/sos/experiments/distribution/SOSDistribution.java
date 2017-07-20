package uk.ac.standrews.cs.sos.experiments.distribution;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDistribution {

    private static final String REMOTE_SOS_JAR_PATH = "Desktop/sos.jar";
    private static final String REMOTE_SOS_CONFIGURATION_PATH = "Desktop/config.conf";
    protected static final String REMOTE_SOS_PID_FILE = "Desktop/sos.pid";
    protected static final String REMOTE_SOS_OUT_FILE = "Desktop/out";


    // This method distributed the app and starts the app too
    public static void distribute(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        String appPath = configuration.getExperimentObj().getSetup().getApp();
        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            NetworkOperations scp = new NetworkOperations();
            scp.setSsh(node.getSsh());
            scp.connect();

            scp.sendFile(appPath, REMOTE_SOS_JAR_PATH);
            scp.sendFile(node.getConfigurationFilePath(), REMOTE_SOS_CONFIGURATION_PATH);

            scp.disconnect();
        }

    }

    public static void startAllApplications(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            NetworkOperations scp = new NetworkOperations();
            scp.setSsh(node.getSsh());
            scp.connect();

            // Run the SOS node with the jetty REST server component
            scp.executeJar(REMOTE_SOS_JAR_PATH, "-c " + REMOTE_SOS_CONFIGURATION_PATH + " -j");

            scp.disconnect();
        }

    }

    public static void stopAllApplications(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            NetworkOperations scp = new NetworkOperations();
            scp.setSsh(node.getSsh());
            scp.connect();

            scp.killProcess(REMOTE_SOS_PID_FILE);
            scp.disconnect();
        }

    }

    public static void distributeToExperimentNode(ExperimentConfiguration configuration) {

    }

    public static void runExperiment(ExperimentConfiguration configuration) {

    }
}

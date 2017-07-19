package uk.ac.standrews.cs.sos.experiments.distribution;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDistribution {

    // This method distributed the app and starts the app too
    public static void distribute(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        String appPath = configuration.getExperimentObj().getSetup().getApp();
        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            NetworkOperations scp = new NetworkOperations();
            scp.ssh = node.getSsh();
            scp.connect();

            scp.sendFile(appPath, "Desktop/sos.jar");
            scp.sendFile(node.getConfigurationFilePath(), "Desktop/config.conf");
            scp.executeJar("Desktop/sos.jar", "-c Desktop/config.conf -j -fs -root 73a7f67f31908dd0e574699f163eda2cc117f7f4");

            scp.disconnect();
        }

    }

    public static void stopAllApplications(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            NetworkOperations scp = new NetworkOperations();
            scp.ssh = node.getSsh();
            scp.connect();

            scp.killProcess("sos.pid");
            scp.disconnect();
        }

    }
}

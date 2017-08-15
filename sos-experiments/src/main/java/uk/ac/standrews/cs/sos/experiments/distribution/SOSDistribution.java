package uk.ac.standrews.cs.sos.experiments.distribution;

import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDistribution {

    private static final String LOCAL_EXPERIMENT_JAR_PATH = "sos-experiments/target/sos-experiments.jar";
    private static final String REMOTE_SOS_JAR_PATH =  "sos.jar";
    private static final String REMOTE_SOS_CONFIGURATION_PATH = "config.json";
    private static final String REMOTE_SOS_PID_FILE =  "sos.pid";
    private static final String REMOTE_SOS_OUT_FILE = "out";
    private static final String REMOTE_SOS_EXPERIMENTS_JAR_PATH =  "sos-experiments.jar";
    private static final String REMOTE_SOS_EXPERIMENT_CONFIGURATION_PATH = "experiment.json";

    public static void distribute(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        String experimentName = configuration.getExperimentObj().getName();
        String appPath = configuration.getExperimentObj().getSetup().getApp();
        System.out.println("Distributing the app at the following path: " + appPath);

        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            System.out.println("Sending files for node " + node.getName());
            String path = node.getPath() + node.getSsh().getUser() + "/";

            NetworkOperations scp = new NetworkOperations();
            scp.setSsh(node.getSsh());
            scp.connect();

            scp.makePath(path);
            scp.sendFile(appPath, path + REMOTE_SOS_JAR_PATH);
            scp.sendFile(node.getConfigurationFile(experimentName), path + REMOTE_SOS_CONFIGURATION_PATH);

            scp.disconnect();
        }

        System.out.println("App distribution finished");
    }

    public static void undoDistribution(ExperimentConfiguration configuration) throws NetworkException {
        System.out.println("UndoDistribution of the app at the remote nodes");

        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            System.out.println("Deleting files for node " + node.getName());
            String path = node.getPath() + node.getSsh().getUser() + "/";

            NetworkOperations scp = new NetworkOperations();
            scp.setSsh(node.getSsh());
            scp.connect();

            scp.deleteFile(path + REMOTE_SOS_OUT_FILE);
            scp.deleteFile(path + REMOTE_SOS_PID_FILE);
            scp.deleteFolder(path + "sos"); // ASSUMING THAT THE NODE USES A SOS FOLDER for the SOS INTERNAL STORAGE

            scp.disconnect();
        }

        System.out.println("App distribution finished");
    }

    public static void startAllApplications(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            String path = node.getPath() + node.getSsh().getUser() + "/";

            NetworkOperations scp = new NetworkOperations();
            scp.setSsh(node.getSsh());
            scp.connect();

            // Run the SOS node with the jetty REST server component
            scp.executeJar(path, REMOTE_SOS_JAR_PATH, "-c " + REMOTE_SOS_CONFIGURATION_PATH + " -j", REMOTE_SOS_OUT_FILE, REMOTE_SOS_PID_FILE);

            scp.disconnect();
        }

    }

    public static void stopAllApplications(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        for(ExperimentConfiguration.Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            String path = node.getPath() + node.getSsh().getUser() + "/";

            NetworkOperations scp = new NetworkOperations();
            scp.setSsh(node.getSsh());
            scp.connect();

            scp.killProcess(path + REMOTE_SOS_PID_FILE);
            scp.disconnect();
        }

    }

    public static void distributeToExperimentNode(ExperimentConfiguration configuration) throws NetworkException, IOException {
        System.out.println("Distributing the SOS-Experiment to a remote node");

        String experimentName = configuration.getExperimentObj().getName();
        ExperimentConfiguration.Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        NetworkOperations scp = new NetworkOperations();
        scp.setSsh(experimentNode.getSsh());
        scp.connect();

        File temp = File.createTempFile("experiment_configuration", ".json");
        temp.deleteOnExit();
        try (PrintWriter printWriter = new PrintWriter(temp)) {
            printWriter.write(configuration.toString());
        }

        scp.makePath(path);
        scp.makePath(path + "experiments/output");
        scp.sendFile(LOCAL_EXPERIMENT_JAR_PATH, path + REMOTE_SOS_EXPERIMENTS_JAR_PATH);
        scp.sendFile(temp.getAbsolutePath(), path + REMOTE_SOS_EXPERIMENT_CONFIGURATION_PATH);
        scp.sendFile(experimentNode.getConfigurationFile(experimentName), path + experimentNode.getConfigurationFile());

        // TODO - copy all necessary stuff for the experiment + data!!!!

        scp.disconnect();
    }

    public static void runExperiment(ExperimentConfiguration configuration) throws NetworkException {
        System.out.println("Running the SOS-Experiment from a remote node");

        ExperimentConfiguration.Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        NetworkOperations scp = new NetworkOperations();
        scp.setSsh(experimentNode.getSsh());
        scp.connect();

        scp.executeJar(path, REMOTE_SOS_EXPERIMENTS_JAR_PATH, "", REMOTE_SOS_OUT_FILE, REMOTE_SOS_PID_FILE);

        scp.disconnect();
    }

    public static void stopExperiment(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        ExperimentConfiguration.Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        NetworkOperations scp = new NetworkOperations();
        scp.setSsh(experimentNode.getSsh());
        scp.connect();

        scp.killProcess(path + REMOTE_SOS_PID_FILE);
        scp.disconnect();
    }

    public static void undoDistributionToExperimentNode(ExperimentConfiguration configuration) throws NetworkException {
        System.out.println("Distributing the SOS-Experiment to a remote node");

        ExperimentConfiguration.Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        NetworkOperations scp = new NetworkOperations();
        scp.setSsh(experimentNode.getSsh());
        scp.connect();

        //scp.deleteFile(path + REMOTE_SOS_OUT_FILE);
        //scp.deleteFile(path + REMOTE_SOS_PID_FILE);
        //scp.deleteFolder(path + "sos"); // ASSUMING THAT THE NODE USES A SOS FOLDER for the SOS INTERNAL STORAGE

        scp.disconnect();
    }
}

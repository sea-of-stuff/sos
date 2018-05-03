package uk.ac.standrews.cs.sos.experiments.distribution;

import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import static uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDistribution {

    private static final String LOCAL_EXPERIMENT_JAR_PATH = "sos-experiments/target/sos-experiment.jar";
    private static final String REMOTE_SOS_JAR_PATH =  "slave.jar";
    private static final String REMOTE_SOS_CONFIGURATION_PATH = "config.json";
    private static final String REMOTE_SOS_CERTIFICATE_PATH = "id_rsa.crt";
    private static final String REMOTE_SOS_KEY_PATH = "id_rsa.key";
    private static final String REMOTE_SOS_PID_FILE =  "sos.pid";
    private static final String REMOTE_SOS_OUT_FILE = "out";
    private static final String REMOTE_SOS_EXPERIMENTS_JAR_PATH =  "sos-experiments.jar";
    public static final String REMOTE_SOS_EXPERIMENT_CONFIGURATION_PATH = "experiment.json";

    public static void distribute(ExperimentConfiguration configuration) throws NetworkException {

        String experimentName = configuration.getExperimentObj().getName();
        String appPath = configuration.getExperimentObj().getSetup().getApp();
        System.out.println("Distributing the app at the following path: " + appPath);
        System.out.println("Number of remote nodes (excluding the main experiment one): " + configuration.getExperimentObj().getNodes().size());

        for(Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            System.out.println("Sending files for node " + node.getName());
            String path = node.getPath() + node.getSsh().getUser() + "/";

            try (NetworkOperations scp = new NetworkOperations()) {
                scp.setSsh(node.getSsh());
                scp.connect();

                scp.makePath(path);
                scp.sendFile(appPath, path + REMOTE_SOS_JAR_PATH, true);
                scp.sendFile(node.getConfigurationFile(experimentName), path + REMOTE_SOS_CONFIGURATION_PATH, true);

                if (node.getCertificateFile() != null && node.getKeyFile() != null) {
                    scp.makePath(path + "sos/node/");
                    scp.sendFile(node.getCertificateFile(experimentName), path + "sos/node/" + REMOTE_SOS_CERTIFICATE_PATH, true);
                    scp.sendFile(node.getKeyFile(experimentName), path + "sos/node/" + REMOTE_SOS_KEY_PATH, true);
                }

                if (node.hasDataset()) {
                    String lDirectoryDataset = REPO_DATASETS_PATH + node.getDataset();
                    scp.sendDirectory(lDirectoryDataset, path + node.getDatasetPath(), false);
                }

            } catch (IOException e) {
                throw new NetworkException();
            }
        }

        System.out.println("App distribution finished");
    }

    public static void undoDistribution(ExperimentConfiguration configuration) throws NetworkException {
        System.out.println("UndoDistribution of the app at the remote nodes");

        for(Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            undoDistribution(node);
        }
    }

    public static void undoDistribution(ExperimentConfiguration configuration, String nodeName) throws NetworkException {
        System.out.println("UndoDistribution of the app at the remote nodes");


        List<Experiment.Node> nodes = configuration.getExperimentObj().getNodes();
        for(Experiment.Node node:nodes) {

            if (node.getName().equals(nodeName)) {

                undoDistribution(node);
                break;
            }
        }
    }

    public static void startAllApplications(ExperimentConfiguration configuration) throws NetworkException {

        for(Experiment.Node node:configuration.getExperimentObj().getNodes()) {
            System.out.println("Starting the node " + node.getName());

            String path = node.getPath() + node.getSsh().getUser() + "/";

            try (NetworkOperations scp = new NetworkOperations()) {
                scp.setSsh(node.getSsh());
                scp.connect();

                // Run the SOS node with the jetty REST server component
                scp.executeJar(path, node.getJava(), REMOTE_SOS_JAR_PATH, "-c " + REMOTE_SOS_CONFIGURATION_PATH + " -j", REMOTE_SOS_OUT_FILE, REMOTE_SOS_PID_FILE);

            } catch (IOException e) {
                throw new NetworkException();
            }
        }

    }

    public static void stopAllApplications(ExperimentConfiguration configuration) throws NetworkException {

        for(Experiment.Node node:configuration.getExperimentObj().getNodes()) {

            stopNode(node);
        }
    }

    public static void stopNode(ExperimentConfiguration configuration, String nodeName) throws NetworkException {

        List<Experiment.Node> nodes = configuration.getExperimentObj().getNodes();
        for(Experiment.Node node:nodes) {

            if (node.getName().equals(nodeName)) {

                stopNode(node);
                break;
            }
        }
    }

    public static void distributeToExperimentNode(ExperimentConfiguration configuration) throws NetworkException {
        System.out.println("Distributing the SOS-Experiment to a remote node");

        String experimentName = configuration.getExperimentObj().getName();
        Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        try (NetworkOperations scp = new NetworkOperations()) {
            scp.setSsh(experimentNode.getSsh());
            scp.connect();

            File temp = File.createTempFile("experiment_configuration", ".json");
            temp.deleteOnExit();
            try (PrintWriter printWriter = new PrintWriter(temp)) {
                printWriter.write(configuration.toString());
            }

            scp.makePath(path);
            scp.makePath(path + "experiments/output");
            scp.sendFile(LOCAL_EXPERIMENT_JAR_PATH, path + REMOTE_SOS_EXPERIMENTS_JAR_PATH, true);
            scp.sendFile(temp.getAbsolutePath(), path + REMOTE_SOS_EXPERIMENT_CONFIGURATION_PATH, true);
            scp.sendFile(experimentNode.getConfigurationFile(experimentName), path + experimentNode.getConfigurationFile(), true);

            if (experimentNode.getCertificateFile() != null && experimentNode.getKeyFile() != null) {
                scp.sendFile(experimentNode.getCertificateFile(experimentName), path + REMOTE_SOS_CERTIFICATE_PATH, true);
                scp.sendFile(experimentNode.getKeyFile(experimentName), path + REMOTE_SOS_KEY_PATH, true);
            }

            if (experimentNode.hasDataset()) {
                System.out.println("Do you want to send the dataset with path: " + experimentNode.getDatasetPath() +
                        " to main experiment node? (y/Y to confirm)");
                Scanner in = new Scanner(System.in);
                String answer = in.nextLine();
                if (answer.toLowerCase().equals("y")) {
                    scp.makePath(path + experimentNode.getDatasetPath());
                    String lDirectoryDataset = REPO_DATASETS_PATH + experimentNode.getDataset();
                    scp.sendDirectory(lDirectoryDataset, path + experimentNode.getDatasetPath(), false);
                } else {
                    System.out.println("Dataset will not be transferred. Make sure that dataset is already in remote experiment node");
                }
            }

            // Copy contexts over
            if (new File(REPO_CONTEXTS_PATH + experimentName + "/").exists()) {
                scp.makePath(path + experimentNode.getContextsPath());
                scp.makePath(path + experimentNode.getContextsPath() + experimentName);
                scp.sendDirectory(REPO_CONTEXTS_PATH + experimentName + "/", path + experimentNode.getContextsPath() + experimentName, true);
            }

            if (experimentNode.isSendUSRO()) {
                scp.makePath(path + experimentNode.getUsroPath());
                scp.sendDirectory(REPO_USRO_PATH, path + experimentNode.getUsroPath(), true);
            }

        } catch (IOException e) {
            throw new NetworkException();
        }
    }

    public static void runExperiment(ExperimentConfiguration configuration, String args) throws NetworkException {
        System.out.println("Running the SOS-Experiment from a remote node");

        Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        try (NetworkOperations scp = new NetworkOperations()) {
            scp.setSsh(experimentNode.getSsh());
            scp.connect();

            scp.executeJar(path, experimentNode.getJava(), REMOTE_SOS_EXPERIMENTS_JAR_PATH, args, REMOTE_SOS_OUT_FILE, REMOTE_SOS_PID_FILE);

        } catch (IOException e) {
            throw new NetworkException();
        }
    }

    public static void stopExperiment(ExperimentConfiguration configuration) throws NetworkException {

        Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        stopNode(experimentNode);
    }

    public static void undoDistributionToExperimentNode(ExperimentConfiguration configuration) throws NetworkException {
        System.out.println("Undoing distribution of the SOS-Experiment to a remote node");

        Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        try (NetworkOperations scp = new NetworkOperations()) {
            scp.setSsh(experimentNode.getSsh());
            scp.connect();

            //scp.deleteFile(path + REMOTE_SOS_OUT_FILE);
            //scp.deleteFile(path + REMOTE_SOS_PID_FILE);
            scp.deleteFolder(path + "sos"); // ASSUMING THAT THE NODE USES A SOS FOLDER for the SOS INTERNAL STORAGE

        } catch (IOException e) {
            throw new NetworkException();
        }
    }

    public static void getFileFromExperimentNode(ExperimentConfiguration configuration, String remote_filepath, String local_filepath) throws NetworkException {
        System.out.println("Getting file from the SOS-Experiment node. File is at remote relative path: " + remote_filepath);

        Experiment.Node experimentNode = configuration.getExperimentObj().getExperimentNode();

        String path = experimentNode.getPath() + experimentNode.getSsh().getUser() + "/";

        try (NetworkOperations scp = new NetworkOperations()) {
            scp.setSsh(experimentNode.getSsh());
            scp.connect();

            scp.getFile(path + remote_filepath, local_filepath);

        } catch (IOException e) {
            throw new NetworkException();
        }

    }

    private static void stopNode(Experiment.Node node) throws NetworkException {

        String path = node.getPath() + node.getSsh().getUser() + "/";

        try (NetworkOperations scp = new NetworkOperations()) {
            scp.setSsh(node.getSsh());
            scp.connect();

            System.out.println("\tKilling process at node: " + node.getName());
            scp.killProcess(path + REMOTE_SOS_PID_FILE);

        } catch (IOException e) {
            throw new NetworkException();
        }

    }

    private static void undoDistribution(Experiment.Node node) throws NetworkException {

        System.out.println("Deleting files for node " + node.getName());
        String path = node.getPath() + node.getSsh().getUser() + "/";

        try (NetworkOperations scp = new NetworkOperations()) {
            scp.setSsh(node.getSsh());
            scp.connect();

            scp.deleteFile(path + REMOTE_SOS_OUT_FILE);
            scp.deleteFile(path + REMOTE_SOS_PID_FILE);
            scp.deleteFolder(path + "sos"); // ASSUMING THAT THE NODE USES A SOS FOLDER for the SOS INTERNAL STORAGE

        } catch (IOException e) {
            throw new NetworkException();
        }
    }
}

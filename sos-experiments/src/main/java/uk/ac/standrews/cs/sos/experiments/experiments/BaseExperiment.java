package uk.ac.standrews.cs.sos.experiments.experiments;

import org.apache.commons.io.FileUtils;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.experiments.*;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.node.BasicNode;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.PingNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.impl.BackgroundInstrument;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseExperiment implements Experiment {

    // Resource locations and output paths
    public static final String CONFIGURATION_FOLDER = "sos-experiments/src/main/resources/experiments/{experiment}/configuration/";
    public static final String CONTEXTS_FOLDER = "sos-experiments/src/main/resources/experiments/{experiment}/contexts/";
    public static final String USRO_FOLDER = "sos-experiments/src/main/resources/experiments/{experiment}/usro/";
    public static final String OUTPUT_FOLDER = "experiments/output/";

    // Experiment timing
    private long start;
    private long end;

    // Node and experiment objects
    protected SOSLocalNode node;
    protected ExperimentConfiguration.Experiment experiment;

    protected ExperimentUnit currentExperimentUnit;
    private int iteration;

    public BaseExperiment(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        this.experiment = experimentConfiguration.getExperimentObj();
        this.iteration = 0;

        String outputFilename = getExperimentResultsFilename();
        prepareExperiment(outputFilename);
    }

    public BaseExperiment(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        this.experiment = experimentConfiguration.getExperimentObj();
        this.iteration = 0;

        prepareExperiment(outputFilename);
    }

    private void prepareExperiment(String outputFilename) throws ExperimentException {

        System.out.println("Total number of iterations for this experiment: " + numberOfTotalIterations());

        try {
            InstrumentFactory.instance(experiment.getStats(), OUTPUT_FOLDER + outputFilename);
            InstrumentFactory.start();

            if (experiment.getExperimentNode().hasDataset()) {
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                InstrumentFactory.instance().measureDataset(new File(datasetPath));
            }

        } catch (IOException e) {
            throw new ExperimentException("Problems while creating the base experiment", e);
        }

        // WarmUp the JVM for the experiments to be runIteration
        WarmUp.run();
    }

    public void setupIteration() throws ExperimentException {

        try {
            String configurationNodePath = experiment.getExperimentNode().getConfigurationFile(experiment.getName());
            File configFile = new File(configurationNodePath);
            if (!configFile.exists()) {
                configFile = new File(experiment.getExperimentNode().getConfigurationFile());
            }

            if (new File("id_rsa.crt").exists() && new File("id_rsa.key").exists()) {
                // Copy node certificate and keys
                makepath("sos/node/");
                FileUtils.copyFile(new File("id_rsa.crt"), new File("sos/node/id_rsa.crt"));
                FileUtils.copyFile(new File("id_rsa.key"), new File("sos/node/id_rsa.key"));
            }

            System.out.println("CONFIG FILE " + configFile.getAbsolutePath());
            SettingsConfiguration configuration = new SettingsConfiguration(configFile);

            node = ServerState.init(configuration.getSettingsObj());

            waitForSlaveNodesToBeRunning();
            node.loadBootstrapNodes(); // Force re-load now that all nodes are running

        } catch (ConfigurationException e) {
            throw new ExperimentException("Unable to process configuration properly", e);
        } catch (IOException e) {
            throw new ExperimentException("Unable to copy configuration files", e);
        } catch (NodeRegistrationException e) {
            throw new ExperimentException("Unable to load bootstrap nodes properly", e);
        }

        currentExperimentUnit = getExperimentUnit();
        currentExperimentUnit.setup();
    }

    private void waitForSlaveNodesToBeRunning() throws ExperimentException {

        int nodesRunning = 0;
        int totalNumberOfNodes = experiment.getNodes().size();
        while(nodesRunning < totalNumberOfNodes) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new ExperimentException("Thread Sleep issue while checking that slave nodes are running");
            }

            // Check that all nodes are available before continuing
            nodesRunning = 0;
            for (ExperimentConfiguration.Experiment.Node node : experiment.getNodes()) {

                Node nodeToPing = new BasicNode(node.getSsh().getHost(), 8080);
                PingNode pingNode = new PingNode(nodeToPing, "HELLO WORLD");

                TasksQueue.instance().performSyncTask(pingNode);
                if (pingNode.getState() == TaskState.SUCCESSFUL) {
                    nodesRunning++;
                }
            }

            System.out.println("Nodes running: " + nodesRunning + " out of " + totalNumberOfNodes + " nodes");
        }

    }

    @Override
    public void runIteration() throws ExperimentException {
        start = System.nanoTime();

        BackgroundInstrument.type = BackgroundInstrument.METRIC_TYPE.experiment;
        currentExperimentUnit.run();
        BackgroundInstrument.type = BackgroundInstrument.METRIC_TYPE.non_experiment;

        end = System.nanoTime();
        long timeToFinish = end - start;
        System.out.println("Experiment iteration {" + (iteration + 1) + "/" + numberOfTotalIterations() + "} finished in " + nanoToSeconds(timeToFinish) + " seconds");
    }

    @Override
    public void finishIteration() throws ExperimentException {
       currentExperimentUnit.finish();

        InstrumentFactory.flush();
        ServerState.kill();
    }

    @Override
    public void cleanup() throws ExperimentException {

        try {
            node.kill();
            node.cleanup();
        } catch (DataStorageException e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void finish() {

        InstrumentFactory.stop();
    }

    @Override
    public void process() throws ExperimentException {

        long start = System.nanoTime();
        for(iteration = 0; iteration < numberOfTotalIterations(); iteration++) {

            setupIteration();
            runIteration();
            finishIteration();

            cleanup(); // NOTE - idea: apply to remote nodes too, which will need to be stopped and restarted

            try {
                System.out.println("Going to sleep for 5 seconds before the next iteration");
                System.out.println("*******************************************************");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new ExperimentException();
            }
        }

        finish();

        long end = System.nanoTime();
        long timeToFinish = end - start;
        System.out.println("All experiments finished in " + nanoToSeconds(timeToFinish) + " seconds");

        try {
            System.out.println("Going to sleep for 5 seconds");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new ExperimentException();
        }
    }

    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations();
    }

    private double nanoToSeconds(long nano) {
        return nano/1000000000.0;
    }

    private String getExperimentResultsFilename() {

        String name = experiment.getName()
                .replace(" ", "_");
        String now =  Instant.now().toString()
                .replace("-", "_")
                .replace(":", "_")
                .replace(".", "_");

        return name + "__" + now;
    }

    public ExperimentConfiguration.Experiment getExperiment() {
        return experiment;
    }

    private static void makepath(String path) {
        java.io.File file = new java.io.File(path);
        java.io.File parent = file.getParentFile();
        if (parent != null)
            parent.mkdirs();

        if (path.endsWith("/")) {
            file.mkdir();
        }
    }
}

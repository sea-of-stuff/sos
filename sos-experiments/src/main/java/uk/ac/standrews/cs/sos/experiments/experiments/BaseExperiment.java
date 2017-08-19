package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.experiments.*;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.OutputTYPE;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseExperiment implements Experiment {

    public static final String CONFIGURATION_FOLDER = "sos-experiments/src/main/resources/experiments/{experiment}/configuration/";
    public static final String OUTPUT_FOLDER = "experiments/output/"; // TODO - specify in experiment config
    public static final String CONTEXTS_FOLDER = "sos-experiments/src/main/resources/experiments/{experiment}/contexts/";
    public static final String TEST_DATA_FOLDER = "sos-experiments/src/main/resources/datasets/text/";

    private long start;

    protected SOSLocalNode node;
    protected ExperimentConfiguration.Experiment experiment;

    protected Iterator<ExperimentUnit> experimentUnitIterator;
    protected ExperimentUnit currentExperimentUnit;

    private int iteration;

    public BaseExperiment(ExperimentConfiguration experimentConfiguration) {
        this.experiment = experimentConfiguration.getExperimentObj();
        this.iteration = 0;

        // WarmUp the JVM for the experiments to be run
        WarmUp.run();
    }

    public void setup() throws ExperimentException {

        try {
            InstrumentFactory.instance(experiment.getStats(), OutputTYPE.TSV, OUTPUT_FOLDER + getExperimentResultsFilename());

            String configurationNodePath = experiment.getExperimentNode().getConfigurationFile(experiment.getName());
            File configFile = new File(configurationNodePath);
            if (!configFile.exists()) {
                configFile = new File(experiment.getExperimentNode().getConfigurationFile());
            }
            System.out.println("CONFIG FILE " + configFile.getAbsolutePath());

            SettingsConfiguration configuration = new SettingsConfiguration(configFile);

            node = ServerState.init(configuration.getSettingsObj());
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
            throw new ExperimentException();
        }

        if (!experimentUnitIterator.hasNext()) throw new ExperimentException();

        currentExperimentUnit = experimentUnitIterator.next();
        currentExperimentUnit.setup();
    }

    @Override
    public void run() throws ExperimentException {
        start = System.nanoTime();

        currentExperimentUnit.run();
    }

    @Override
    public void finish() {
        long end = System.nanoTime();
        long timeToFinish = end - start;
        System.out.println("Experiment iteration {" + iteration + "} finished in " + nanoToSeconds(timeToFinish) + " seconds");

        ServerState.kill();
    }

    @Override
    public void cleanup() throws ExperimentException {

        try {
            // TODO - this delete everything! not sure if this is a good idea ...
            node.cleanup();
        } catch (DataStorageException e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void process() throws ExperimentException {

        for(iteration = 0; iteration < numberOfTotalIterations(); iteration++) {

            setup();
            run();
            finish();

            cleanup();
        }
    }

    public abstract int numberOfTotalIterations();

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
}

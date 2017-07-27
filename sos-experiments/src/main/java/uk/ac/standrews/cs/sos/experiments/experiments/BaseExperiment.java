package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ServerState;
import uk.ac.standrews.cs.sos.experiments.WarmUp;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.OutputTYPE;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseExperiment implements Experiment {

    public static final String CONFIGURATION_FOLDER = "sos-experiments/src/main/resources/experiments/{experiment}/configuration/";
    public static final String OUTPUT_FOLDER = "sos-experiments/src/main/resources/output/"; // TODO - change to experiments/data/ where the R scripts are
    public static final String CONTEXTS_FOLDER = "sos-experiments/src/main/resources/experiments/{experiment}/contexts/";
    public static final String TEST_DATA_FOLDER = "sos-experiments/src/main/resources/data/";

    private long start;

    protected SOSLocalNode node;
    protected ExperimentConfiguration.Experiment experiment;

    public BaseExperiment(ExperimentConfiguration experimentConfiguration) {
        this.experiment = experimentConfiguration.getExperimentObj();

        // WarmUp the JVM for the experiments to be run
        WarmUp.run();
    }

    public void setup() throws ExperimentException {

        try {
            InstrumentFactory.instance(experiment.getStats(), OutputTYPE.CSV, OUTPUT_FOLDER + getExperimentResultsFilename());

            String configurationNodePath = experiment.getExperimentNode().getConfigurationFile(experiment.getName());
            File configFile = new File(configurationNodePath);
            SettingsConfiguration configuration = new SettingsConfiguration(configFile);

            node = ServerState.init(configuration.getSettingsObj());
        } catch (ConfigurationException | IOException e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void run() throws ExperimentException {
        start = System.nanoTime();
    }

    @Override
    public void finish() {
        long end = System.nanoTime();
        long timeToFinish = end - start;
        System.out.println("Experiment process in " + nanoToSeconds(timeToFinish) + " seconds");
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

        for(int i = 0; i < numberOfTotalIterations(); i++) {

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
        String extension = ".csv";

        return name + "__" + now + extension;
    }
}

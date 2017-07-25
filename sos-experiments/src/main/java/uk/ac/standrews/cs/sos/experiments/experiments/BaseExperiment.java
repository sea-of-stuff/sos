package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ServerState;
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

    public static final String EXPERIMENTS_FOLDER = "sos-experiments/src/main/resources/experiments/";
    public static final String CONFIGURATION_FOLDER = "configuration/";
    public static final String OUTPUT_FOLDER = "sos-experiments/src/main/resources/output/";

    protected SOSLocalNode node;

    private long start;
    private long end;

    private ExperimentConfiguration.Experiment experiment;

    public BaseExperiment(ExperimentConfiguration experimentConfiguration) {
        this.experiment = experimentConfiguration.getExperimentObj();
    }

    public void setup() throws ExperimentException {

        try {
            InstrumentFactory.instance(experiment.getStats(), OutputTYPE.CSV, OUTPUT_FOLDER + getExperimentResultsFilename());

            String configurationNodePath = experiment.getExperimentNode().getConfigurationFilePath();
            File configFile = new File(configurationNodePath);
            SettingsConfiguration configuration = new SettingsConfiguration(configFile);

            node = ServerState.init(configuration.getSettingsObj());
        } catch (ConfigurationException | IOException e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void start() {
        start = System.nanoTime();
    }

    @Override
    public void finish() {
        end = System.nanoTime();
    }

    @Override
    public void cleanup() throws ExperimentException {

        try {
            node.cleanup(); // TODO - this delete everything! not sure if this is a good idea ...
        } catch (DataStorageException e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void collectStats() {
        long timeToFinish = end - start;
        System.out.println("Experiment run in " + nanoToSeconds(timeToFinish) + " seconds");
    }

    @Override
    public void run() throws ExperimentException {

        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {

            setup();
            start();
            finish();

            collectStats();
            cleanup();
        }
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
        String extension = ".csv";

        return name + "__" + now + extension;
    }
}

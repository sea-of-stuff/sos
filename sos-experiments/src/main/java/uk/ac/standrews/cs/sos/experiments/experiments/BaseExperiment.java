package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseExperiment implements Experiment {

    public static final String CONFIGURATION_FOLDER = "sos-experiments/src/main/resources/configurations/";

    private long start;
    private long end;

    @Override
    public void start() {
        start = System.nanoTime();
    }

    @Override
    public void finish() {
        end = System.nanoTime();
    }

    @Override
    public void cleanup() {

        // TODO - delete all the downloaded content, so that the next experiment is run clean
    }

    @Override
    public void collectStats() {
        long timeToFinish = end - start;
        System.out.println("Experiment run in " + timeToFinish /1000000000.0 + " seconds");
    }

    @Override
    public void run() throws Exception {
        setup();
        start();
        finish();
        collectStats();

        cleanup();
    }
}

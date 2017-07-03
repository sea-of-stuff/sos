package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseExperiment implements Experiment {

    private long start, end, timeToFinish;

    @Override
    public void start() {
        start = System.nanoTime();
    }

    @Override
    public void finish() {
        end = System.nanoTime();
    }

    @Override
    public void collectStats() {
        timeToFinish = end - start;
        System.out.println("Experiment run in " + timeToFinish/1000000000.0 + " seconds");
    }
}

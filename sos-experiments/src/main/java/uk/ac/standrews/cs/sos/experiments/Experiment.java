package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

/**
 * TODO - must be able to deal with different settings and should be able to process the experiment multiple times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Experiment {

    /**
     * This method should be called to setupIteration the SOS environment before the experiment is process
     * @throws ExperimentException if the experiment could not be setupIteration
     */
    void setupIteration() throws ExperimentException;

    /**
     * Start the actual experiment
     */
    void runIteration() throws ExperimentException;

    /**
     * Mark the end of the experiment iteration
     */
    void finishIteration() throws ExperimentException;

    /**
     * Cleans up any caches, data, states that are left over from the experiment
     */
    void cleanup() throws ExperimentException;

    void finish() throws ExperimentException;

    /**
     * Run all the steps for the experiment, from setupIteration to cleanup
     * @throws ExperimentException if any of the steps of the experiment could not be process properly
     */
    void process() throws ExperimentException;

    int numberOfTotalIterations();

    // NOTE (1) Experiments should have a main method to runIteration as standalone java programs.
    // NOTE (2) This is important if we want to runIteration the experiment from remote while having control using our local machine

}

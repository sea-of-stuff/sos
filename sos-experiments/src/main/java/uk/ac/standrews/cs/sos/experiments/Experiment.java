package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

/**
 * TODO - must be able to deal with different settings and should be able to process the experiment multiple times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Experiment {

    /**
     * This method should be called to setup the SOS environment before the experiment is process
     * @throws ExperimentException if the experiment could not be setup
     */
    void setup() throws ExperimentException;

    /**
     * Start the actual experiment
     */
    void run() throws ExperimentException;

    /**
     * Mark the end of the experiment
     */
    void finish() throws ExperimentException;

    /**
     * Cleans up any caches, data, states that are left over from the experiment
     */
    void cleanup() throws ExperimentException;

    /**
     * Run all the steps for the experiment, from setup to cleanup
     * @throws ExperimentException if any of the steps of the experiment could not be process properly
     */
    void process() throws ExperimentException;

    int numberOfTotalIterations();

}

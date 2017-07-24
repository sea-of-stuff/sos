package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

/**
 * TODO - must be able to deal with different settings and should be able to run the experiment multiple times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Experiment {

    /**
     * This method should be called to setup the SOS environment before the experiment is run
     * @throws ExperimentException if the experiment could not be setup
     */
    void setup() throws ExperimentException;

    /**
     * Start the actual experiment
     */
    void start() throws ExperimentException;

    /**
     * Mark the end of the experiment
     */
    void finish() throws ExperimentException;

    /**
     * Cleans up any caches, data, states that are left over from the experiment
     */
    void cleanup() throws ExperimentException;

    /**
     * Collect the wanted stats to a file
     */
    void collectStats() throws ExperimentException;

    /**
     * Run all the steps for the experiment, from setup to cleanup
     * @throws ExperimentException if any of the steps of the experiment could not be run properly
     */
    void run() throws ExperimentException;

}

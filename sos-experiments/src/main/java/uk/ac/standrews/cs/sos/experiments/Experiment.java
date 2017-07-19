package uk.ac.standrews.cs.sos.experiments;

/**
 * TODO - must be able to deal with different settings and should be able to run the experiment multiple times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Experiment {

    /**
     * This method should be called to setup the SOS environment before the experiment is run
     * @throws Exception if the experiment could not be setup
     */
    void setup() throws Exception; // FIXME - use a custom made exception

    /**
     * Start the actual experiment
     */
    void start();

    /**
     * Mark the end of the experiment
     */
    void finish();

    /**
     * Cleans up any caches, data, states that are left over from the experiment
     */
    void cleanup();

    /**
     * Collect the wanted stats to a file
     */
    void collectStats();

    /**
     * Run all the steps for the experiment, from setup to cleanup
     * @throws Exception if any of the steps of the experiment could not be run properly
     */
    void run() throws Exception;

}

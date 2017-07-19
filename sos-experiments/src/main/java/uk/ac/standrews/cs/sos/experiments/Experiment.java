package uk.ac.standrews.cs.sos.experiments;

/**
 * TODO - must be able to deal with different settings and should be able to run the experiment multiple times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Experiment {

    void setup() throws Exception;

    void start();

    void finish() throws Exception;

    // TODO - save stats to file
    void collectStats();

}

package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ExperimentUnit {

    /**
     * The setup for the experiment unit
     *
     * @throws ExperimentException
     */
    void setup() throws ExperimentException;

    /**
     * The code that runs the actual experiment for this unit
     *
     * @throws ExperimentException
     */
    void run() throws ExperimentException;;
}

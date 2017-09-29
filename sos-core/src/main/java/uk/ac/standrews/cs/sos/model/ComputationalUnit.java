package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ComputationalUnit extends Manifest {

    /**
     * The list of dependency for the computational unit to be compiled and run.
     *
     * @return
     */
    JsonNode dependencies();

    /**
     * The code to compile and run.
     * This will define the uniqueness of this computational unit.
     *
     * @return
     */
    JsonNode code();
}

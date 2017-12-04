package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This interface indicates that the object implementing it is a computational unit.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ComputationalUnit extends Manifest {

    /**
     * JsonNode with the java dependencies for the computational unit.
     *
     * @return json node.
     */
    JsonNode dependencies();
}

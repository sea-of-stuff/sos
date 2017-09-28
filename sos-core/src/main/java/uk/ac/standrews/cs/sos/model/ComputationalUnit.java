package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ComputationalUnit extends Manifest {

    JsonNode dependencies();

    JsonNode code();
}

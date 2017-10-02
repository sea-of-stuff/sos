package uk.ac.standrews.cs.sos.impl.context.reflection;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ClassBuilder {

    String className(JsonNode jsonNode) throws IOException;

    String constructClass(JsonNode jsonNode) throws IOException;
}

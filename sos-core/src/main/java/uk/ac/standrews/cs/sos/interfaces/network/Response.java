package uk.ac.standrews.cs.sos.interfaces.network;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Response {

    int getCode();

    InputStream getBody();

    JsonNode getJSON();

    String getStringBody() throws IOException;
}

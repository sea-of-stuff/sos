package uk.ac.standrews.cs.sos.interfaces.network;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP Response wrapper
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Response {

    /**
     *
     * @return the HTTP response code
     */
    int getCode();

    /**
     *
     * @return the body of the response as an input stream
     */
    InputStream getBody();

    /**
     *
     * @return the body of the response as a JSON object
     */
    JsonNode getJSON();

    /**
     *
     * @return the body of the response as a string
     */
    String getStringBody();

    /**
     * Content Lenght in bytes
     * @return length of the response body
     */
    int getContentLength();

    /**
     * Consume resources used by the response.
     * @throws IOException if unable to consume response.
     */
    void consumeResponse() throws IOException;
}

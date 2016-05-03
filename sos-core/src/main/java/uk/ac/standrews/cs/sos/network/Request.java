package uk.ac.standrews.cs.sos.network;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Request {

    // HTTP requests
    // must have a state

    private String method; // GET, PUT, etc

    private int respondeCode;
    private String response;
}

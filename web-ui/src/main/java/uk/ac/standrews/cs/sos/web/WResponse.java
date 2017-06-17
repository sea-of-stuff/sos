package uk.ac.standrews.cs.sos.web;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WResponse {

    private final String responseMessage;
    private final int responseCode;

    public WResponse(final String responseMessage, final int responseCode) {
        this.responseMessage = responseMessage;
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return this.responseMessage;
    }

    public int getStatus() {
        return this.responseCode;
    }
}
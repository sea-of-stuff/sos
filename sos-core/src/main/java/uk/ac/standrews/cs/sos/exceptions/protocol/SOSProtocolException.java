package uk.ac.standrews.cs.sos.exceptions.protocol;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSProtocolException extends Exception {

    public SOSProtocolException(Error e) {
        super(e);
    }

    public SOSProtocolException(String message) {
        super(message);
    }

    public SOSProtocolException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

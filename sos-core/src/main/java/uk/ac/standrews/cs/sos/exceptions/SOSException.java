package uk.ac.standrews.cs.sos.exceptions;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSException extends Exception {

    public SOSException(Throwable throwable) {
        super(throwable);
    }
    public SOSException(String message, Throwable throwable) {
        super(message, throwable);
    }
    public SOSException(String message) {
        super(message);
    }
}

package uk.ac.standrews.cs.sos.exceptions.location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SourceLocationException extends Exception {

    public SourceLocationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SourceLocationException(Throwable throwable) {
        super(throwable);
    }
}

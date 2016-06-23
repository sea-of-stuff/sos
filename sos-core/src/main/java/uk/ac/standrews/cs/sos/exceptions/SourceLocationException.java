package uk.ac.standrews.cs.sos.exceptions;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SourceLocationException extends Exception {

    public SourceLocationException(String message) {
        super(message);
    }

    public SourceLocationException(Throwable throwable) {
        super(throwable);
    }
}

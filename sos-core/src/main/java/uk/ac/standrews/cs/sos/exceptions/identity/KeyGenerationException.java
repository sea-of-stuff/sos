package uk.ac.standrews.cs.sos.exceptions.identity;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class KeyGenerationException extends IdentityException {

    public KeyGenerationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public KeyGenerationException(String message) {
        super(message);
    }
}

package uk.ac.standrews.cs.sos.exceptions.crypto;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class KeyGenerationException extends CryptoException {

    public KeyGenerationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public KeyGenerationException(String message) {
        super(message);
    }
}

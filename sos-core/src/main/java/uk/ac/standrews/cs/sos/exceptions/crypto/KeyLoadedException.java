package uk.ac.standrews.cs.sos.exceptions.crypto;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class KeyLoadedException extends CryptoException {

    public KeyLoadedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

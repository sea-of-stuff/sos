package uk.ac.standrews.cs.sos.exceptions.crypto;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CryptoException extends SOSException {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(Throwable throwable) {
        super(throwable);
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

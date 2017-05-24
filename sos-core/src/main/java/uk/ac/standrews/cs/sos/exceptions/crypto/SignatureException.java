package uk.ac.standrews.cs.sos.exceptions.crypto;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SignatureException extends SOSException {

    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(Throwable throwable) {
        super(throwable);
    }

    public SignatureException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

package uk.ac.standrews.cs.sos.exceptions.crypto;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ProtectionException extends SOSException {

    public ProtectionException(String message) {
        super(message);
    }

    public ProtectionException(Throwable throwable) {
        super(throwable);
    }

    public ProtectionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

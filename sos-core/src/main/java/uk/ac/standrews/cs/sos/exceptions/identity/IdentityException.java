package uk.ac.standrews.cs.sos.exceptions.identity;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityException extends SOSException {

    public IdentityException(String message) {
        super(message);
    }

    public IdentityException(Throwable throwable) {
        super(throwable);
    }

    public IdentityException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

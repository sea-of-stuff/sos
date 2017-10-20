package uk.ac.standrews.cs.sos.exceptions.node;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeRegistrationException extends SOSException {

    public NodeRegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NodeRegistrationException(String message) {
        super(message);
    }
}

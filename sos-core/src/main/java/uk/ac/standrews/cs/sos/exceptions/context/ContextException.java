package uk.ac.standrews.cs.sos.exceptions.context;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextException extends SOSException {

    public ContextException() {
        super();
    };

    public ContextException(Throwable throwable) {
        super(throwable);
    }

    public ContextException(String message) {
        super(message);
    }
}

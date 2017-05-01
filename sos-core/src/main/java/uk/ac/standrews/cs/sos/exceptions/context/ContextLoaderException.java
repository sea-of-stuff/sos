package uk.ac.standrews.cs.sos.exceptions.context;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoaderException extends SOSException {

    public ContextLoaderException(String message) {
        super(message);
    }

    public ContextLoaderException(Throwable throwable) {
        super(throwable);
    }
}

package uk.ac.standrews.cs.sos.exceptions.reflection;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClassLoaderException extends SOSException {

    public ClassLoaderException(String message) {
        super(message);
    }

    public ClassLoaderException(Throwable throwable) {
        super(throwable);
    }
}

package uk.ac.standrews.cs.sos.exceptions.index;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IndexException extends SOSException {

    public IndexException(String message) {
        super(message);
    }

    public IndexException(Throwable throwable) {
        super(throwable);
    }

}

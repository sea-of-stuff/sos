package uk.ac.standrews.cs.sos.exceptions.storage;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorageException extends SOSException {

    public DataStorageException() {
        super();
    }

    public DataStorageException(Throwable throwable) {
        super(throwable);
    }

    public DataStorageException(String message) {
        super(message);
    }
}

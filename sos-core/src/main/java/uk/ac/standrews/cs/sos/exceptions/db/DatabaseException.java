package uk.ac.standrews.cs.sos.exceptions.db;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabaseException extends SOSException {

    public DatabaseException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DatabaseException(Throwable throwable) {
        super(throwable);
    }

    public DatabaseException(String message) {
        super(message);
    }
}

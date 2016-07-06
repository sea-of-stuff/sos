package uk.ac.standrews.cs.sos.storage.exceptions;

/**
 * Indicates a problem with persistence.
 *
 * @author graham
 */
public class PersistenceException extends Exception {

    public PersistenceException(String msg) {
        super(msg);
    }

    public PersistenceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

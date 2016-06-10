package uk.ac.standrews.cs.sos.exceptions.db;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabasePersistenceException extends Exception {

    public DatabasePersistenceException(String message) {
        super(message);
    }

    public DatabasePersistenceException(Throwable throwable) {
        super(throwable);
    }
}

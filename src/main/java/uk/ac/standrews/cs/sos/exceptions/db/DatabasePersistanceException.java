package uk.ac.standrews.cs.sos.exceptions.db;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabasePersistanceException extends Exception {

    public DatabasePersistanceException() {
        super();
    }

    public DatabasePersistanceException(String message) {
        super(message);
    }

    public DatabasePersistanceException(Throwable throwable) {
        super(throwable);
    }
}

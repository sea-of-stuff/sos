package uk.ac.standrews.cs.sos.exceptions.db;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabaseConnectionException extends DatabaseException {

    public DatabaseConnectionException(Throwable throwable) {
        super(throwable);
    }
}
package uk.ac.standrews.cs.sos.exceptions.userrole;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UserRolePersistException extends Exception {

    public UserRolePersistException() {
        super();
    }

    public UserRolePersistException(String message) {
        super(message);
    }

    public UserRolePersistException(Throwable throwable) {
        super(throwable);
    }
}

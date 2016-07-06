package uk.ac.standrews.cs.sos.storage.exceptions;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageException extends Exception {

    public StorageException() {
        super();
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }

}

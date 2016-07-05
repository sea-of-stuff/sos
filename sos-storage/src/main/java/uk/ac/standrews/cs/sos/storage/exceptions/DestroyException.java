package uk.ac.standrews.cs.sos.storage.exceptions;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DestroyException extends StorageException {

    public DestroyException(String message) {
        super(message);
    }

    public DestroyException(Throwable throwable) {
        super(throwable);
    }
}

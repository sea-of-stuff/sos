package uk.ac.standrews.cs.sos.exceptions.manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestManagerException extends Exception {

    public ManifestManagerException() {
        super();
    }

    public ManifestManagerException(Throwable throwable) {
        super(throwable);
    }

    public ManifestManagerException(String message) {
        super(message);
    }

    public ManifestManagerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

package uk.ac.standrews.cs.sos.exceptions.manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsDirectoryException extends ManifestException {

    public ManifestsDirectoryException(Throwable throwable) {
        super(throwable);
    }

    public ManifestsDirectoryException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

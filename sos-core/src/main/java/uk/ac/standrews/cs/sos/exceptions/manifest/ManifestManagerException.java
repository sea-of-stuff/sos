package uk.ac.standrews.cs.sos.exceptions.manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestManagerException extends ManifestException {

    public ManifestManagerException(Throwable throwable) {
        super(throwable);
    }

    public ManifestManagerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

package uk.ac.standrews.cs.sos.exceptions.manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestNotMadeException extends ManifestException {

    public ManifestNotMadeException(String message) {
        super(message);
    }

    public ManifestNotMadeException(String message, Exception throwable) {
        super(message, throwable);
    }
}

package uk.ac.standrews.cs.sos.exceptions.manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestNotFoundException extends ManifestException {

    public ManifestNotFoundException(String message, Throwable throwable) {
        super("Exception: " + message, throwable);
    }

    public ManifestNotFoundException(String message) {
        super("Exception: " + message);
    }

    public ManifestNotFoundException() {
        super();
    }
}

package uk.ac.standrews.cs.sos.exceptions.manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestVerificationFailedException extends ManifestException {

    public ManifestVerificationFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

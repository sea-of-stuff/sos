package uk.ac.standrews.cs.sos.exceptions.manifest;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestException extends SOSException {

    public ManifestException() {
        super();
    }

    public ManifestException(Throwable throwable) {
        super(throwable);
    }

    public ManifestException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ManifestException(String message) {
        super(message);
    }
}

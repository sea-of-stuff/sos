package uk.ac.standrews.cs.sos.exceptions.metadata;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSMetadataException extends SOSException {

    public SOSMetadataException(Throwable throwable) {
        super(throwable);
    }

    public SOSMetadataException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SOSMetadataException(String message) {
        super(message);
    }
}

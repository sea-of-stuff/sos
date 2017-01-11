package uk.ac.standrews.cs.sos.exceptions.metadata;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataException extends SOSException {

    public MetadataException(Throwable throwable) {
        super(throwable);
    }

    public MetadataException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MetadataException(String message) {
        super(message);
    }
}

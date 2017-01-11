package uk.ac.standrews.cs.sos.exceptions.metadata;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataNotFoundException extends SOSException {

    public MetadataNotFoundException(String message) {
        super(message);
    }
}

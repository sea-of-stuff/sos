package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.storage.data.Data;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataEngine {

    SOSMetadata processData(Data data) throws SOSMetadataException;

    // NOTES
    // save metadata
    // link
    // add/update
    // get data from metadata?
}

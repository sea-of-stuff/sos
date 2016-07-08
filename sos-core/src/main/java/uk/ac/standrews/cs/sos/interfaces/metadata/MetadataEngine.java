package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.storage.data.Data;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataEngine {

    Metadata processData(Data data);
}

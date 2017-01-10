package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.interfaces.actors.MCS;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.storage.data.InputStreamData;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSMCS implements MCS {

    private MetadataEngine engine;

    public SOSMCS(MetadataEngine metadataEngine) {
        this.engine = metadataEngine;
    }

    // TODO - rename to process metadata
    // save/get metadata from DDS?
    @Override
    public SOSMetadata processMetadata(InputStream inputStream) throws SOSMetadataException {

        InputStreamData data = new InputStreamData(inputStream);
        SOSMetadata metadata = engine.processData(data);

        return metadata;

    }
}

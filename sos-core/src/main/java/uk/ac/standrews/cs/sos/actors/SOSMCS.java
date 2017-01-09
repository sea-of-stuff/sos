package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.interfaces.actors.MCS;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataDirectory;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.model.metadata.MetadataDirectoryImpl;
import uk.ac.standrews.cs.sos.storage.LocalStorage;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSMCS implements MCS {

    private MetadataDirectory metadataDirectory;

    public SOSMCS(LocalStorage localStorage, MetadataEngine metadataEngine, MetadataPolicy metadataPolicy) {
        this.metadataDirectory = new MetadataDirectoryImpl(localStorage, metadataEngine, metadataPolicy);
    }

    @Override
    public SOSMetadata addMetadata(InputStream inputStream) throws SOSMetadataException {
        SOSMetadata metadata = metadataDirectory.processMetadata(inputStream);
        metadataDirectory.addMetadata(metadata);

        return metadata;
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) {
        SOSMetadata metadata = metadataDirectory.getMetadata(guid);
        return metadata;
    }
}

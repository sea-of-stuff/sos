package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.MMS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataDirectory;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.model.metadata.MetadataDirectoryImpl;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.data.InputStreamData;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSMMS implements MMS {

    private MetadataEngine engine;
    private MetadataDirectory metadataDirectory;

    public SOSMMS(LocalStorage localStorage, MetadataEngine metadataEngine, MetadataPolicy metadataPolicy, NDS nds) {

        this.engine = metadataEngine;
        metadataDirectory = new MetadataDirectoryImpl(localStorage, metadataEngine, metadataPolicy, nds);
    }


    @Override
    public void addMetadata(SOSMetadata metadata) throws MetadataPersistException {
        metadataDirectory.addMetadata(metadata);
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) throws MetadataNotFoundException {
        return metadataDirectory.getMetadata(guid);
    }

    @Override
    public SOSMetadata processMetadata(InputStream inputStream) throws MetadataException {

        InputStreamData data = new InputStreamData(inputStream);
        SOSMetadata metadata = engine.processData(data);

        return metadata;
    }

}

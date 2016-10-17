package uk.ac.standrews.cs.sos.metadata;

import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataManager;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.storage.data.InputStreamData;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataManagerImpl implements MetadataManager {

    private MetadataEngine engine;
    private MetadataPolicy policy;
    // CACHE
    // STORE

    public MetadataManagerImpl(MetadataEngine engine, MetadataPolicy policy) {
        this.engine = engine;
        this.policy = policy;
    }

    @Override
    public void addMetadata(InputStream inputStream) throws SOSMetadataException {

        InputStreamData data = new InputStreamData(inputStream);
        SOSMetadata metadata = engine.processData(data);

        cache(metadata);
        replicate(metadata);
    }

    private void cache(SOSMetadata metadata) {
        // TODO
    }

    private void replicate(SOSMetadata metadata) {
        if (policy.replicationFactor() > 0) {
            replicate(metadata, null); // TODO
        }
    }

    private void replicate(SOSMetadata metadata, Node node) {
        // TODO
    }
}

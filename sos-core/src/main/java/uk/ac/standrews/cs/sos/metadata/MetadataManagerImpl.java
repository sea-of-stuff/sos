package uk.ac.standrews.cs.sos.metadata;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataManager;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.storage.InternalStorage;
import uk.ac.standrews.cs.storage.data.InputStreamData;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataManagerImpl implements MetadataManager {

    private InternalStorage internalStorage;
    private MetadataEngine engine;
    private MetadataPolicy policy;

    private List<SOSMetadata> naiveCache = new ArrayList<>();

    public MetadataManagerImpl(InternalStorage internalStorage, MetadataEngine engine, MetadataPolicy policy) {
        this.internalStorage = internalStorage;
        this.engine = engine;
        this.policy = policy;
    }

    @Override
    public SOSMetadata addMetadata(InputStream inputStream) throws SOSMetadataException {

        InputStreamData data = new InputStreamData(inputStream);
        SOSMetadata metadata = engine.processData(data);

        save(metadata);
        cache(metadata);
        replicate(metadata);

        return metadata;
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) {
        // Look for metadata and return it

        // check cache
        // if cache miss, then get it from disk
        // fill cache and return
        // if cannot get from disk, then try to get
        // from another node
        // fill the cache and return

        return null;
    }

    @Override
    public List<IGUID> getVersions(String attribute, String value) {
        // cache is fundamental here, otherwise we need to look at the disk which is not great
        return null;
    }

    private void save(SOSMetadata metadata) {
        try {
            Directory directory = internalStorage.getMetadataDirectory();

            File metadataFile = internalStorage.createFile(directory, metadata.guid().toString());
            StringData data = new StringData(metadata.tabularFormat());
            metadataFile.setData(data);
            metadataFile.persist();

        } catch (DataStorageException | PersistenceException | DataException e) {
            e.printStackTrace();
        }

    }

    private void cache(SOSMetadata metadata) {
        naiveCache.add(metadata);
    }

    private void replicate(SOSMetadata metadata) {
        if (policy.replicationFactor() > 0) {
            replicate(metadata, null); // TODO
        }
    }

    private void replicate(SOSMetadata metadata, Node node) {
        // TODO - use network layer for this
    }

    // WORK IN PROGRESS CODE
    private void processMetadata(Version version) {
        // TODO - get metadata
    }
}

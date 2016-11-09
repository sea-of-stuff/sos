package uk.ac.standrews.cs.sos.model.metadata;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataManager;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaIgnoreMetadata;
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
    public SOSMetadata processMetadata(InputStream inputStream) throws SOSMetadataException {

        InputStreamData data = new InputStreamData(inputStream);
        SOSMetadata metadata = engine.processData(data);

        return metadata;
    }

    @Override
    public void addMetadata(SOSMetadata metadata) {
        save(metadata);
        cache(metadata);
        replicate(metadata);
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) {
        // Look for metadata and return it
        // NOT FOUND EXCEPTION

        // check cache
        // if cache miss, then get it from disk
        // fill cache and return
        // if cannot get from disk, then try to get
        // from another node
        // fill the cache and return

        SOSMetadata metadata = findMetadataFromCache(guid);
        if (metadata == null) {
            metadata = findMetadataLocal(guid);
        }

        return metadata;
    }

    @Override
    public List<IGUID> getVersions(String attribute, String value) {
        // cache is fundamental here, otherwise we need to look at the disk which is not great
        // triplestore
        return null;
    }

    private void save(SOSMetadata metadata) {
        try {
            Directory directory = internalStorage.getMetadataDirectory();

            File metadataFile = internalStorage.createFile(directory, metadata.guid().toString());
            StringData data = new StringData(metadata.tabularFormat());
            metadataFile.setData(data);
            metadataFile.persist();

        } catch (DataStorageException | PersistenceException | DataException | GUIDGenerationException e) {
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

    private SOSMetadata findMetadataFromCache(IGUID guid) {
        // TODO - make it faster using hashtable
        for(SOSMetadata metadata:naiveCache) {
            try {
                if (metadata.guid().equals(guid)) {
                    return metadata;
                }
            } catch (GUIDGenerationException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private SOSMetadata findMetadataLocal(IGUID guid) {

        try {
            Directory directory = internalStorage.getMetadataDirectory();
            File metadataFile = internalStorage.createFile(directory, guid.toString());

            SOSMetadata metadata = new BasicMetadata(metadataFile, TikaIgnoreMetadata.IGNORE_METADATA);

            return metadata;
        } catch (DataStorageException e) {
            e.printStackTrace();
        }


        return null;
    }


}

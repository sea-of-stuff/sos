package uk.ac.standrews.cs.sos.model.metadata;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.protocol.MetadataReplication;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataDirectory;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.model.metadata.basic.BasicMetadata;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaIgnoreMetadata;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.data.InputStreamData;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataDirectoryImpl implements MetadataDirectory {

    private LocalStorage localStorage;
    private MetadataEngine engine;
    private MetadataPolicy policy;

    private List<SOSMetadata> naiveCache = new CopyOnWriteArrayList<>(); // NOTE - can make it faster using hashtable?

    private NDS nds;

    public MetadataDirectoryImpl(LocalStorage localStorage, MetadataEngine engine, MetadataPolicy policy, NDS nds) {
        this.localStorage = localStorage;
        this.engine = engine;
        this.policy = policy;

        this.nds = nds;
    }

    @Override
    public SOSMetadata processMetadata(InputStream inputStream) throws MetadataException {

        InputStreamData data = new InputStreamData(inputStream);
        return engine.processData(data);
    }

    @Override
    public void addMetadata(SOSMetadata metadata) { // TODO - throw persist exception
        save(metadata);
        cache(metadata);
        replicate(metadata);
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) throws MetadataNotFoundException {

        SOSMetadata metadata = findMetadataFromCache(guid);
        if (metadata == null) {
            metadata = findMetadataLocal(guid);
        }

        if (metadata == null) {
            metadata = findMetadataRemote(guid);
        }

        if (metadata == null) {
            throw new MetadataNotFoundException("Unable to find metadata for GUID: " + guid);
        }

        // Make sure that metadata is saved locally and cached for further usage
        save(metadata);
        cache(metadata);

        return metadata;
    }

    @Override
    public List<IGUID> getVersions(String attribute, String value) {
        // cache is fundamental here, otherwise we need to look at the disk which is not great
        // use triplestore?
        return null;
    }

    private void save(SOSMetadata metadata) {
        try {
            Directory directory = localStorage.getMetadataDirectory();

            File metadataFile = localStorage.createFile(directory, metadata.guid().toString());
            StringData data = new StringData(metadata.toString());
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
            try {
                Iterator<Node> nodes = nds.getDDSNodesIterator();
                MetadataReplication.Replicate(metadata, nodes, policy.replicationFactor());
            } catch (SOSProtocolException e) {
                e.printStackTrace();
                //throw new ManifestPersistException("Unable to replicate metadata");
            }
        }
    }

    private SOSMetadata findMetadataFromCache(IGUID guid) {

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
            Directory directory = localStorage.getMetadataDirectory();
            File metadataFile = localStorage.createFile(directory, guid.toString());

            SOSMetadata metadata = new BasicMetadata(metadataFile, TikaIgnoreMetadata.IGNORE_METADATA);

            return metadata;
        } catch (DataStorageException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SOSMetadata findMetadataRemote(IGUID guid) {
        // TODO - not implemented yet

        // Iterate over nodes
        // Return as soon as metadata is fetched
        // FetchMetadata.Fetch(null, guid);
        return null;
    }

}

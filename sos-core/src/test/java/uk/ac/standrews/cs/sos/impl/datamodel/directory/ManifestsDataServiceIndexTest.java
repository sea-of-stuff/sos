package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsDataServiceIndexTest {

    @Test
    public void basicTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        IGUID nodeGUID = GUIDFactory.generateRandomGUID();

        ManifestsLocationsIndex index = new ManifestsLocationsIndex();
        index.addEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDs = index.getNodeRefs(manifestGUID);
        assertNotNull(nodesGUIDs);
        assertNotEquals(nodesGUIDs.size(), 0);
        assertEquals(nodesGUIDs.size(), 1);
        assertTrue(nodesGUIDs.contains(nodeGUID));
    }

    @Test
    public void nullTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID();

        ManifestsLocationsIndex index = new ManifestsLocationsIndex();
        Set<IGUID> nodesGUIDs = index.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDs.size(), 0);
    }

    @Test
    public void evictTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        IGUID nodeGUID = GUIDFactory.generateRandomGUID();

        ManifestsLocationsIndex index = new ManifestsLocationsIndex();
        index.addEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDs = index.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDs.size(), 1);

        index.evictEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDsSecondTime = index.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDsSecondTime.size(), 0);
    }

    @Test
    public void persistIndexTest() throws IOException, ClassNotFoundException, DataStorageException, StorageException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);

        LocalStorage localStorage = new LocalStorage(stor);
        IDirectory cachesDir = localStorage.getNodeDirectory();

        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        IGUID nodeGUID = GUIDFactory.generateRandomGUID();

        ManifestsLocationsIndex manifestsLocationsIndex = new ManifestsLocationsIndex();
        manifestsLocationsIndex.addEntry(manifestGUID, nodeGUID);

        IFile file = localStorage.createFile(cachesDir, "dds.index");
        Persistence.Persist(manifestsLocationsIndex, file);

        ManifestsLocationsIndex persistedIndex = (ManifestsLocationsIndex) Persistence.Load(file);
        Set<IGUID> nodesGUIDs = persistedIndex.getNodeRefs(manifestGUID);
        assertNotNull(nodesGUIDs);
        assertNotEquals(nodesGUIDs.size(), 0);
        assertEquals(nodesGUIDs.size(), 1);
        assertTrue(nodesGUIDs.contains(nodeGUID));

    }

    @Test
    public void persistEmptyIndexTest() throws IOException, ClassNotFoundException, DataStorageException, StorageException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);

        LocalStorage localStorage = new LocalStorage(stor);
        IDirectory cachesDir = localStorage.getNodeDirectory();

        ManifestsLocationsIndex manifestsLocationsIndex = new ManifestsLocationsIndex();

        IFile file = localStorage.createFile(cachesDir, "dds.index");
        Persistence.Persist(manifestsLocationsIndex, file);

        ManifestsLocationsIndex persistedIndex = (ManifestsLocationsIndex) Persistence.Load(file);
        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        Set<IGUID> nodesGUIDs = persistedIndex.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDs.size(), 0);
    }
}
package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.storage.LocalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DDSIndexTest {

    @Test
    public void basicTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        IGUID nodeGUID = GUIDFactory.generateRandomGUID();

        DDSIndex index = new DDSIndex();
        index.addEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDs = index.getDDSRefs(manifestGUID);
        assertNotNull(nodesGUIDs);
        assertNotEquals(nodesGUIDs.size(), 0);
        assertEquals(nodesGUIDs.size(), 1);
        assertTrue(nodesGUIDs.contains(nodeGUID));
    }

    @Test
    public void nullTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID();

        DDSIndex index = new DDSIndex();
        Set<IGUID> nodesGUIDs = index.getDDSRefs(manifestGUID);
        assertNull(nodesGUIDs);
    }

    @Test
    public void evictTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        IGUID nodeGUID = GUIDFactory.generateRandomGUID();

        DDSIndex index = new DDSIndex();
        index.addEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDs = index.getDDSRefs(manifestGUID);
        assertEquals(nodesGUIDs.size(), 1);

        index.evictEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDsSecondTime = index.getDDSRefs(manifestGUID);
        assertEquals(nodesGUIDsSecondTime.size(), 0);
    }

    @Test
    public void persistIndexTest() throws IOException, ClassNotFoundException, DataStorageException, StorageException {

        LocalStorage localStorage = new LocalStorage(StorageFactory.createStorage(StorageType.LOCAL, System.getProperty("user.home") + "/sos/"));
        Directory cachesDir = localStorage.getNodeDirectory();

        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        IGUID nodeGUID = GUIDFactory.generateRandomGUID();

        DDSIndex ddsIndex = new DDSIndex();
        ddsIndex.addEntry(manifestGUID, nodeGUID);

        File file = localStorage.createFile(cachesDir, "dds.index");
        ddsIndex.persist(file);

        DDSIndex persistedIndex = DDSIndex.load(file);
        Set<IGUID> nodesGUIDs = persistedIndex.getDDSRefs(manifestGUID);
        assertNotNull(nodesGUIDs);
        assertNotEquals(nodesGUIDs.size(), 0);
        assertEquals(nodesGUIDs.size(), 1);
        assertTrue(nodesGUIDs.contains(nodeGUID));

    }

    @Test
    public void persistEmptyIndexTest() throws IOException, ClassNotFoundException, DataStorageException, StorageException {

        LocalStorage localStorage = new LocalStorage(StorageFactory.createStorage(StorageType.LOCAL, System.getProperty("user.home") + "/sos/"));
        Directory cachesDir = localStorage.getNodeDirectory();

        DDSIndex ddsIndex = new DDSIndex();

        File file = localStorage.createFile(cachesDir, "dds.index");
        ddsIndex.persist(file);

        DDSIndex persistedIndex = DDSIndex.load(file);

        IGUID manifestGUID = GUIDFactory.generateRandomGUID();
        Set<IGUID> nodesGUIDs = persistedIndex.getDDSRefs(manifestGUID);
        assertNull(nodesGUIDs);
    }
}
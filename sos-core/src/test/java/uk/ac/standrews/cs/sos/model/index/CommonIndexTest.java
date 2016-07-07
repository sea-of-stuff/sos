package uk.ac.standrews.cs.sos.model.index;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.Config;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonIndexTest {

    private InternalStorage storage;
    private Index index;

    @BeforeMethod
    public void setUp(Method method) throws IndexException, StorageException, DataStorageException {
        Config config = new Config();
        storage = new InternalStorage(StorageFactory.createStorage(config.s_type, config.s_location, true));
        index = LuceneIndex.getInstance(storage);
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException, DataStorageException {
        index.flushDB();
        index.killInstance();

        storage.destroy();
    }

    @Test(expectedExceptions = IndexException.class)
    public void testAddManifestWithWrongType() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("WrongType");

        index.addManifest(simpleManifestMocked);
    }
}

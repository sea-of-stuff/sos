package uk.ac.standrews.cs.sos.model.storage;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class InternalStorageTest extends CommonTest {

    protected InternalStorage internalStorage;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        internalStorage =
                new InternalStorage(StorageFactory.createStorage(StorageType.LOCAL, "~/sos/", true));
    }

    @AfterMethod
    public void tearDown() throws DataStorageException {
        internalStorage.destroy();
    }

    @Test
    public void defaultDirectoriesExistTest() throws DataStorageException {
        assertNotNull(internalStorage.getDataDirectory());
        assertNotNull(internalStorage.getManifestDirectory());
        assertNotNull(internalStorage.getHeadsDirectory());
        assertNotNull(internalStorage.getMetadataDirectory());
    }

    @Test
    public void createFileTest() throws DataStorageException, BindingAbsentException,
            PersistenceException, DataException {

        internalStorage.createFile(internalStorage.getManifestDirectory(),
                "test.txt").persist();

        File file = (File) internalStorage.getManifestDirectory().get("test.txt");
        assertNotNull(file);

        assertEquals(file.getData().getSize(), 0);
    }

    @Test
    public void createFileWithDataTest() throws DataStorageException, BindingAbsentException,
            PersistenceException, DataException {

        internalStorage.createFile(internalStorage.getManifestDirectory(),
                "test.txt", new StringData("test-data")).persist();

        File file = (File) internalStorage.getManifestDirectory().get("test.txt");
        assertNotNull(file);

        assertEquals(file.getData().getSize(), 9);
    }

}

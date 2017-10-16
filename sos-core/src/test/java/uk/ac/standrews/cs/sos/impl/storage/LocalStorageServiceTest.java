package uk.ac.standrews.cs.sos.impl.storage;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalStorageServiceTest extends CommonTest {

    protected LocalStorage localStorage;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        localStorage = new LocalStorage(stor);
    }

    @AfterMethod
    public void tearDown() throws DataStorageException {
        localStorage.destroy();
    }

    @Test
    public void defaultDirectoriesExistTest() throws DataStorageException {
        assertNotNull(localStorage.getDataDirectory());
        assertNotNull(localStorage.getManifestsDirectory());
    }

    @Test
    public void createFileTest() throws DataStorageException, BindingAbsentException,
            PersistenceException, DataException {

        localStorage.createFile(localStorage.getManifestsDirectory(),
                "test.txt").persist();

        IFile file = (IFile) localStorage.getManifestsDirectory().get("test.txt");
        assertNotNull(file);

        assertEquals(file.getData().getSize(), 0);
    }

    @Test
    public void createFileWithDataTest() throws DataStorageException, BindingAbsentException,
            PersistenceException, DataException {

        localStorage.createFile(localStorage.getManifestsDirectory(),
                "test.txt", new StringData("test-data")).persist();

        IFile file = (IFile) localStorage.getManifestsDirectory().get("test.txt");
        assertNotNull(file);

        assertEquals(file.getData().getSize(), 9);
    }

}

package uk.ac.standrews.cs.sos.impl.storage;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalStorageServiceTest extends SetUpTest {

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

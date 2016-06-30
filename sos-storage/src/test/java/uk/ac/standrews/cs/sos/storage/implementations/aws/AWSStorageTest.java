package uk.ac.standrews.cs.sos.storage.implementations.aws;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.data.StringData;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorageTest {

    private static final String AWS_S3_TEST_BUCKET = "sos-simone-test";
    private static final Data TEST_DATA = new StringData("hello world");

    private Storage storage;

    @BeforeMethod
    public void setUp() {
        storage = new AWSStorage(AWS_S3_TEST_BUCKET, false);
    }

    @AfterMethod
    public void tearDown() {
        // TODO - delete info in bucket
    }

    @Test
    public void createFile() throws PersistenceException {
        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();
        assertTrue(file.exists());
    }

    @Test
    public void checkFileInDirectory() throws PersistenceException {
        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();
        assertTrue(storage.getTestDirectory().contains("test.txt"));
    }

    @Test
    public void checkFileInDirectoryFails() throws PersistenceException {
        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();

        // Checking file in the wrong directory
        assertFalse(storage.getDataDirectory().contains("test.txt"));
    }
}
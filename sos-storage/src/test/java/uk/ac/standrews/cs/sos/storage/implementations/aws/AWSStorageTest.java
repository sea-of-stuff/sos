package uk.ac.standrews.cs.sos.storage.implementations.aws;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.data.StringData;
import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;
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

    @Test
    public void getFileFromDirectory() throws PersistenceException, BindingAbsentException {
        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();

        StatefulObject object = storage.getTestDirectory().get("test.txt");
        // TODO - assert that object is a file!
    }

    @Test
    public void dummyTest() throws PersistenceException {
        storage.createFile(storage.getTestDirectory(), "test-1.txt", TEST_DATA).persist();
        storage.createFile(storage.getTestDirectory(), "test-2.txt", TEST_DATA).persist();
        storage.createFile(storage.getTestDirectory(), "test-3.txt", TEST_DATA).persist();
        storage.createFile(storage.getTestDirectory(), "test-4.txt", TEST_DATA).persist();
        storage.createFile(storage.getTestDirectory(), "test-5.txt", TEST_DATA).persist();

        int testCounter = 0;
        Iterator it = storage.getTestDirectory().getIterator();
        while(it.hasNext()) {
            it.next();
            testCounter++;
        }

        assertEquals(testCounter, 5);
    }
}
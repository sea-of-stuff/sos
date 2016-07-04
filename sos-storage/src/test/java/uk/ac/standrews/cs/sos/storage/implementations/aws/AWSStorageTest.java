package uk.ac.standrews.cs.sos.storage.implementations.aws;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.data.StringData;
import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.exceptions.DataException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.implementations.CommonStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.*;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorageTest {

    private static final String AWS_S3_TEST_BUCKET = "sos-simone-test";
    private static final Data TEST_DATA = new StringData("hello world");
    private static final int TEST_DELAY = 1000; // Needed to allow AWS to perform background ops

    private Storage storage;

    @BeforeMethod
    public void setUp() {
        storage = new AWSStorage(AWS_S3_TEST_BUCKET, false);
    }

    @AfterMethod
    public void tearDown() throws BindingAbsentException, InterruptedException {
        ((AWSStorage) storage).deleteBucket();

        Thread.sleep(TEST_DELAY);
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
    public void fileInDirectoryDeletion() throws PersistenceException, BindingAbsentException {
        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();
        assertTrue(storage.getTestDirectory().contains("test.txt"));

        storage.getTestDirectory().remove("test.txt");
        assertFalse(storage.getTestDirectory().contains("test.txt"));
    }

    @Test
    public void directoryDeletion() throws PersistenceException, BindingAbsentException {
        assertTrue(storage.getRoot().contains(CommonStorage.TEST_DATA_DIRECTORY_NAME + "/"));

        storage.getRoot().remove(CommonStorage.TEST_DATA_DIRECTORY_NAME + "/");
        Iterator iterator = storage.getTestDirectory().getIterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void getFileFromDirectory() throws PersistenceException, BindingAbsentException {
        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();

        StatefulObject object = storage.getTestDirectory().get("test.txt");
        assertTrue(object instanceof AWSFile);
    }

    @Test
    public void iteratorTest() throws PersistenceException {

        // Populate folder
        for(int i = 0 ; i < 15; i++) {
            storage.createFile(storage.getTestDirectory(), "test-" + i + ".txt", TEST_DATA).persist();
        }

        int testCounter = 0;
        Iterator<NameObjectBinding> it = storage.getTestDirectory().getIterator();
        while(it.hasNext()) {
            NameObjectBinding objectBinding = it.next();
            testCounter++;
        }

        assertEquals(testCounter, 16); // Expecting parent directory too
    }

    @Test
    public void folderWithFileExistsTest() throws PersistenceException {
        Directory directory = storage.createDirectory(storage.getTestDirectory(), "folder_with_file");
        storage.createFile(directory, "empty_file.txt").persist();

        assertTrue(storage.getTestDirectory().contains("folder_with_file/"));
    }

    @Test
    public void emptyFolderExistsTest() throws PersistenceException {
        storage.createDirectory(storage.getTestDirectory(), "empty_folder").persist();

        boolean contains = storage.getTestDirectory().contains("empty_folder/");
        assertTrue(contains);
    }

    @Test
    public void folderWithFolderExistsTest() throws PersistenceException {
        Directory directory = storage.createDirectory(storage.getTestDirectory(), "folder_with_folder");
        storage.createDirectory(directory, "inner_folder").persist();

        boolean contains = storage.getTestDirectory().contains("folder_with_folder/");
        assertTrue(contains);
    }

    @Test
    public void getDataTest() throws PersistenceException, DataException {

        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();

        File retrievedFile = storage.createFile(storage.getTestDirectory(), "test.txt");
        assertNotNull(retrievedFile.getData());
    }

}
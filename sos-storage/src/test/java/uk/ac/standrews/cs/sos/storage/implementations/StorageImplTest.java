package uk.ac.standrews.cs.sos.storage.implementations;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.data.StringData;
import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.exceptions.DataException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.NameObjectBinding;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageImplTest extends StorageBaseTest {

    private static final Data TEST_DATA = new StringData("hello world");
    private final STORAGE_TYPE storageType;

    @Factory(dataProvider = "storage-manager-provider")
    public StorageImplTest(STORAGE_TYPE storageType) {
        this.storageType = storageType;
    }

    @Override
    protected STORAGE_TYPE getStorageType() {
        return storageType;
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
        assertTrue(object instanceof File);
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
            it.next().getName();
            testCounter++;
        }

        assertEquals(testCounter, 15); // Expecting parent directory too
    }

    @Test
    public void folderWithFileTest() throws PersistenceException {
        Directory directory = storage.createDirectory(storage.getTestDirectory(), "folder_with_file");
        storage.createFile(directory, "empty_file.txt").persist();

        boolean contains = storage.getTestDirectory().contains("folder_with_file/");
        assertTrue(contains);
    }

    @Test
    public void emptyFolderTest() throws PersistenceException {
        storage.createDirectory(storage.getTestDirectory(), "empty_folder");

        boolean contains = storage.getTestDirectory().contains("empty_folder/");
        assertFalse(contains);
    }

    @Test
    public void emptyFolderPersistedTest() throws PersistenceException {
        storage.createDirectory(storage.getTestDirectory(), "empty_folder").persist();

        boolean contains = storage.getTestDirectory().contains("empty_folder/");
        assertTrue(contains);
    }

    @Test
    public void folderWithFolderTest() throws PersistenceException {
        Directory directory = storage.createDirectory(storage.getTestDirectory(), "folder_with_folder");
        storage.createDirectory(directory, "inner_folder");

        boolean contains = storage.getTestDirectory().contains("folder_with_folder/");
        assertFalse(contains);

        boolean containsInner = directory.contains("inner_folder/");
        assertFalse(containsInner);
    }

    @Test
    public void nestedFolderWithFileTest() throws PersistenceException {
        Directory directory = storage.createDirectory(storage.getTestDirectory(), "folder_with_folder");
        Directory innerDirectory = storage.createDirectory(directory, "inner_folder");
        File file = storage.createFile(innerDirectory, "test.txt", TEST_DATA);
        file.persist();

        boolean contains = storage.getTestDirectory().contains("folder_with_folder/");
        assertTrue(contains);

        boolean containsInner = directory.contains("inner_folder/");
        assertTrue(containsInner);
    }

    @Test
    public void nestedFolderPersistedTest() throws PersistenceException {
        Directory directory = storage.createDirectory(storage.getTestDirectory(), "folder_with_folder");
        storage.createDirectory(directory, "inner_folder").persist();

        boolean contains = storage.getTestDirectory().contains("folder_with_folder/");
        assertTrue(contains);

        boolean containsInner = directory.contains("inner_folder/");
        assertTrue(containsInner);
    }

    @Test
    public void getDataTest() throws PersistenceException, DataException {
        File file = storage.createFile(storage.getTestDirectory(), "test.txt", TEST_DATA);
        file.persist();

        File retrievedFile = storage.createFile(storage.getTestDirectory(), "test.txt");
        assertNotNull(retrievedFile.getData());
    }

}

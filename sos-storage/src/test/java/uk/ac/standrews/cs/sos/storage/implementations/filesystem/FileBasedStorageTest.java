package uk.ac.standrews.cs.sos.storage.implementations.filesystem;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.data.StringData;
import uk.ac.standrews.cs.sos.storage.interfaces.NameObjectBinding;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.File;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedStorageTest {

    private static final File ROOT_TEST_DIRECTORY = new File("/tmp/storage/");
    private static final Data TEST_DATA = new StringData("hello world");

    private Storage storage;

    @BeforeMethod
    public void setUp() {
        storage = new FileBasedStorage(ROOT_TEST_DIRECTORY, false);
    }

    @Test
    public void testGetIterator() throws Exception {

        // Populate folder
        for(int i = 0 ; i < 15; i++) {
            storage.createFile(storage.getTestDirectory(), "test-" + i + ".txt", TEST_DATA).persist();
        }

        int testCounter = 0;
        Iterator<NameObjectBinding> it = storage.getTestDirectory().getIterator();
        while(it.hasNext()) {
            it.next();
            testCounter++;
        }

        assertEquals(testCounter, 15); // FIXME - Expecting parent directory too. should be 16

    }
}
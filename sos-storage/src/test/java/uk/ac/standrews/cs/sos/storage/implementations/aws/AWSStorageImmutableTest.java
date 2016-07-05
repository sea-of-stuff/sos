package uk.ac.standrews.cs.sos.storage.implementations.aws;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.data.StringData;
import uk.ac.standrews.cs.sos.storage.exceptions.DestroyException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorageImmutableTest {

    private static final String AWS_S3_TEST_BUCKET = "sos-simone-test";
    private static final Data TEST_DATA = new StringData("hello world");

    private static final int TEST_DELAY = 1000; // Needed to allow any background ops

    private Storage storage;

    @BeforeMethod
    public void setUp() {
        storage = new AWSStorage(AWS_S3_TEST_BUCKET, true);
    }

    @AfterMethod
    public void tearDown() throws DestroyException, InterruptedException {
        storage.destroy();

        Thread.sleep(TEST_DELAY);
    }

    @Test
    public void createFile() throws PersistenceException {
        File file = storage.createFile(storage.getTestDirectory(), "test-immutable.txt", TEST_DATA);
        file.persist();

        long lastModified = file.lastModified();

        File cloneFile = storage.createFile(storage.getTestDirectory(), "test-immutable.txt", TEST_DATA);
        cloneFile.persist();
        long lastModifiedClone = cloneFile.lastModified();

        assertEquals(lastModified, lastModifiedClone);
    }
}

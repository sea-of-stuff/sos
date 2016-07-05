package uk.ac.standrews.cs.sos.storage.implementations;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import uk.ac.standrews.cs.sos.storage.exceptions.DestroyException;
import uk.ac.standrews.cs.sos.storage.exceptions.StorageException;
import uk.ac.standrews.cs.sos.storage.implementations.aws.AWSStorage;
import uk.ac.standrews.cs.sos.storage.implementations.filesystem.FileBasedStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static uk.ac.standrews.cs.sos.storage.implementations.StorageBaseTest.STORAGE_TYPE.AWS;
import static uk.ac.standrews.cs.sos.storage.implementations.StorageBaseTest.STORAGE_TYPE.LOCAL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class StorageBaseTest {

    private static final String AWS_S3_TEST_BUCKET = "sos-simone-test";
    private static final File ROOT_TEST_DIRECTORY = new File("/tmp/storage/");

    private static final int TEST_DELAY = 1000; // Needed to allow any background ops


    protected abstract STORAGE_TYPE getStorageType();
    protected Storage storage;

    @BeforeMethod
    public void setUp(Method method) throws StorageException {
        STORAGE_TYPE type = getStorageType();
        System.out.println(type.toString() + " :: " + method.getName());
        storage = new StorageFactory().getStorage(type);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException, DestroyException {
        storage.destroy();

        Thread.sleep(TEST_DELAY);
    }

    @DataProvider(name = "storage-manager-provider")
    public static Object[][] indexProvider() throws IOException {
        return new Object[][] {
                {LOCAL}, {AWS}
        };
    }

    public enum STORAGE_TYPE {
        LOCAL, AWS
    }

    public class StorageFactory {

        public Storage getStorage(STORAGE_TYPE type) throws StorageException {
            switch(type) {
                case LOCAL:
                    return new FileBasedStorage(ROOT_TEST_DIRECTORY, false);
                case AWS:
                    return new AWSStorage(AWS_S3_TEST_BUCKET, false);
            }
            return null;
        }
    }
}

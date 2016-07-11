package uk.ac.standrews.cs.sos.model.index;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.index.IndexException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.lang.reflect.Method;

import static uk.ac.standrews.cs.sos.model.index.IndexBaseTest.INDEX_TYPE.LUCENE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class IndexBaseTest {

    protected abstract INDEX_TYPE getIndexType();

    protected InternalStorage storage;
    protected Index index;

    @BeforeMethod
    public void setUp(Method method) throws IndexException, StorageException, DataStorageException {
        INDEX_TYPE type = getIndexType();
        System.out.println(type.toString() + " :: " + method.getName());

        Config config = new Config();
        storage = new InternalStorage(StorageFactory.createStorage(config.s_type, config.s_location, true));
        index = new IndexFactory().getIndex(storage, type);
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException, DataStorageException {
        index.flushDB();
        index.killInstance();

        storage.destroy();
    }

    @DataProvider(name = "index-manager-provider")
    public static Object[][] indexProvider() throws IOException {
        return new Object[][] {
                {LUCENE}
        };
    }

    public enum INDEX_TYPE {
        LUCENE
    }

    public class IndexFactory {

        public Index getIndex(InternalStorage storage, INDEX_TYPE type) throws IndexException {
            switch(type) {
                case LUCENE:
                    return LuceneIndex.getInstance(storage);
            }
            return null;
        }
    }
}

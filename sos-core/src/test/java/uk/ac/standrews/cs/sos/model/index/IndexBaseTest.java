package uk.ac.standrews.cs.sos.model.index;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;
import java.lang.reflect.Method;

import static uk.ac.standrews.cs.sos.model.index.IndexBaseTest.CACHE_TYPE.LUCENE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class IndexBaseTest {

    protected abstract CACHE_TYPE getCacheType();
    protected Index index;

    @BeforeMethod
    public void setUp(Method method) throws IndexException, ConfigurationException {
        CACHE_TYPE type = getCacheType();
        System.out.println(type.toString() + " :: " + method.getName());
        index = new CacheFactory().getCache(type);
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException, ConfigurationException {
        index.flushDB();
        index.killInstance();

        HelperTest.DeletePath(Configuration.getInstance().getIndexDirectory());
    }

    @DataProvider(name = "index-manager-provider")
    public static Object[][] cacheProvider() throws IOException {
        return new Object[][] {
                {LUCENE}
        };
    }

    public enum CACHE_TYPE {
        LUCENE
    }

    public class CacheFactory {

        public Index getCache(CACHE_TYPE type) throws IndexException {
            switch(type) {
                case LUCENE:
                    return LuceneIndex.getInstance();
            }
            return null;
        }
    }
}

package uk.ac.standrews.cs.sos.managers;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.configurations.TestConfiguration;

import java.io.File;
import java.io.IOException;

import static uk.ac.standrews.cs.sos.managers.CacheBaseTest.CACHE_TYPE.LUCENE;
import static uk.ac.standrews.cs.sos.managers.CacheBaseTest.CACHE_TYPE.REDIS;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CacheBaseTest {

    public abstract CACHE_TYPE getCacheType();
    protected MemCache cache;

    @BeforeMethod
    public void setUp() throws IOException {
        CACHE_TYPE type = getCacheType();
        System.out.println("Running test for cache " + type.toString());
        cache = new CacheFactory().getCache(type);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        cache.flushDB();
        cache.killInstance();

        // FIXME - directory is never deleted!
        FileUtils.deleteDirectory(new File(cache.getConfiguration().getIndexPath()));
    }

    @DataProvider(name = "cache-manager-provider")
    public static Object[][] cacheProvider() throws IOException {
        return new Object[][] {
                {REDIS},
                {LUCENE}
        };
    }

    public enum CACHE_TYPE {
        REDIS, LUCENE
    }

    public class CacheFactory {

        public MemCache getCache(CACHE_TYPE type, SeaConfiguration configuration) throws IOException {
            switch(type) {
                case LUCENE:
                    return LuceneCache.getInstance(configuration);
                case REDIS:
                    return RedisCache.getInstance(configuration);
            }
            return null;
        }

        public MemCache getCache(CACHE_TYPE type) throws IOException {
            SeaConfiguration configuration = new TestConfiguration();
            return getCache(type, configuration);
        }
    }
}

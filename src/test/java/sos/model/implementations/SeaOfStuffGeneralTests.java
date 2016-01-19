package sos.model.implementations;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sos.configurations.SeaConfiguration;
import sos.configurations.TestConfiguration;
import sos.exceptions.KeyGenerationException;
import sos.exceptions.KeyLoadedException;
import sos.exceptions.UnknownGUIDException;
import sos.managers.MemCache;
import sos.managers.RedisCache;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.interfaces.SeaOfStuff;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffGeneralTests {

    private SeaOfStuff model;
    private MemCache cache;
    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() {
        try {
            configuration = new TestConfiguration();
            cache = RedisCache.getInstance();
            model = new SeaOfStuffImpl(configuration, cache);
        } catch (KeyGenerationException e) {
            e.printStackTrace();
        } catch (KeyLoadedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() {
        cache.flushDB();
        cache.killInstance();
    }

    @Test(expectedExceptions = UnknownGUIDException.class)
    public void testFailRetrieveManifest() throws Exception {
        model.getManifest(new GUIDsha1("123fail"));
    }

    @Test (expectedExceptions = UnknownGUIDException.class)
    public void testFailRetrieveManifestNull() throws Exception {
        model.getManifest(null);
    }
}

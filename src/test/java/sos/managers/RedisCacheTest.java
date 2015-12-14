package sos.managers;

import IO.utils.StreamsUtils;
import constants.Hashes;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import redis.embedded.RedisServer;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.utils.GUIDsha1;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RedisCacheTest {

    private RedisServer server;
    private static final int REDIS_PORT = 6380; // 6379 is the default one - do not use it

    @BeforeTest
    public void setUp() throws IOException {
        server = new RedisServer(REDIS_PORT);
        server.start();
    }

    @AfterTest
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddSimpleManifest() throws Exception {
        AtomManifest atomManifestMocked = mock(AtomManifest.class);
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        when(atomManifestMocked.getManifestGUID()).thenReturn(new GUIDsha1(inputStreamFake));
        when(atomManifestMocked.getManifestType()).thenReturn("Atom");

        RedisCache cache = RedisCache.getInstance();
        cache.addManifest(atomManifestMocked);
        assertEquals("Atom", cache.getManifestType(atomManifestMocked.getManifestGUID()));
    }
}
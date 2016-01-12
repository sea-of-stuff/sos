package sos.managers;

import IO.utils.StreamsUtils;
import constants.Hashes;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import redis.embedded.RedisServer;
import sos.exceptions.UnknownManifestTypeException;
import sos.model.implementations.components.manifests.AssetManifest;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.implementations.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RedisCacheTest {

    private MemCache cache;
    private RedisServer server;

    @BeforeMethod
    public void setUp() throws IOException {
        server = new RedisServer(RedisCache.REDIS_PORT);
        server.start();

        cache = RedisCache.getInstance();
    }

    @AfterMethod
    public void tearDown() {
        cache.killInstance();
        server.stop();
    }

    @Test
    public void testAddSimpleManifest() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        InputStream otherInputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1((otherInputStreamFake)));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        GUID contentGUID = simpleManifestMocked.getContentGUID();

        cache.addManifest(simpleManifestMocked);
        assertEquals(cache.getManifestType(contentGUID), "Atom");
    }

    @Test (expectedExceptions = UnknownManifestTypeException.class)
    public void testAddManifestWithWrongType() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        InputStream otherInputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1((otherInputStreamFake)));
        when(simpleManifestMocked.getManifestType()).thenReturn("WrongType");

        cache.addManifest(simpleManifestMocked);
    }

    @Test
    public void testAddAtomManifest() throws Exception {
        AtomManifest atomManifestMocked = mock(AtomManifest.class);
        InputStream otherInputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);

        when(atomManifestMocked.getContentGUID()).thenReturn(new GUIDsha1((otherInputStreamFake)));
        when(atomManifestMocked.getManifestType()).thenReturn("Atom");

        Location locationMocked = mock(URLLocation.class);
        Location otherLocationMocked = mock(URLLocation.class);
        Collection<Location> locations = new ArrayList<Location>(
                Arrays.asList(locationMocked, otherLocationMocked)
        );
        when(atomManifestMocked.getLocations()).thenReturn(locations);
        when(locationMocked.toString()).thenReturn("Location_1");
        when(otherLocationMocked.toString()).thenReturn("Location_2");

        GUID contentGUID = atomManifestMocked.getContentGUID();
        cache.addManifest(atomManifestMocked);

        Collection<String> cachedLocations = cache.getLocations(contentGUID);
        for(String location:cachedLocations) {
            assertContains(location, "Location_1", "Location_2");
        }
    }

    @Test
    public void testAddCompoundManifest() throws Exception {
        CompoundManifest compoundManifestMocked = mock(CompoundManifest.class);
        InputStream otherInputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);

        when(compoundManifestMocked.getContentGUID()).thenReturn(new GUIDsha1((otherInputStreamFake)));
        when(compoundManifestMocked.getManifestType()).thenReturn("Compound");
        when(compoundManifestMocked.getSignature()).thenReturn("Test_Signature");

        Content contentMocked = mock(Content.class);
        Content otherContentMocked = mock(Content.class);
        Collection<Content> contents = new ArrayList<Content>(
                Arrays.asList(contentMocked, otherContentMocked)
        );
        when(compoundManifestMocked.getContents()).thenReturn(contents);
        when(contentMocked.toString()).thenReturn("Content_1");
        when(otherContentMocked.toString()).thenReturn("Content_2");


        GUID contentGUID = compoundManifestMocked.getContentGUID();
        cache.addManifest(compoundManifestMocked);
        assertEquals(cache.getSignature(contentGUID), "Test_Signature");

        Collection<String> cachedContents = cache.getContents(contentGUID);
        for(String content:cachedContents) {
            assertContains(content, "Content_1", "Content_2");
        }
    }

    @Test
    public void testAddAssetManifest() throws Exception {
        AssetManifest assetManifestMocked = mock(AssetManifest.class);

        InputStream otherInputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        InputStream metaInputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        InputStream incarnationInputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);

        when(assetManifestMocked.getContentGUID()).thenReturn(new GUIDsha1(otherInputStreamFake));
        when(assetManifestMocked.getAssetGUID()).thenReturn(new GUIDsha1(incarnationInputStreamFake));
        when(assetManifestMocked.getManifestType()).thenReturn("Asset");
        when(assetManifestMocked.getSignature()).thenReturn("Test_Signature");

        GUID metadataFake = new GUIDsha1(metaInputStreamFake);
        Collection<GUID> metadata = new ArrayList<GUID>(
                Arrays.asList(metadataFake)
        );
        when(assetManifestMocked.getMetadataGUID()).thenReturn(metadata);

        GUID previousOne = mock(GUID.class);
        GUID previousTwo = mock(GUID.class);
        Collection<GUID> prevs = new ArrayList<GUID>(
                Arrays.asList(previousOne, previousTwo)
        );
        when(assetManifestMocked.getPreviousManifests()).thenReturn(prevs);
        when(previousOne.toString()).thenReturn("PREV_1");
        when(previousTwo.toString()).thenReturn("PREV_2");

        Content contentMocked = mock(Content.class);
        when(assetManifestMocked.getContent()).thenReturn(contentMocked);
        when(contentMocked.toString()).thenReturn("Content_1");

        GUID contentGUID = assetManifestMocked.getContentGUID();
        cache.addManifest(assetManifestMocked);
        assertEquals(cache.getSignature(contentGUID), "Test_Signature");

        Collection<String> cachedPrevs = cache.getPrevs(contentGUID);
        for(String prev:cachedPrevs) {
            assertContains(prev, "PREV_1", "PREV_2");
        }

        Collection<String> cachedMetadata = cache.getMetadata(contentGUID);
        for(String meta:cachedMetadata) {
            assertContains(meta, Hashes.TEST_STRING_HASHED);
        }

        assertEquals(cache.getIncarnation(contentGUID), Hashes.TEST_STRING_HASHED);
    }

    // This must be used, because Redis sets do not preserve ordering
    // http://stackoverflow.com/questions/17801314/how-to-use-or-condition-with-testng-assertions
    private void assertContains(Object actual, Object ... expected) {
        assertTrue(Arrays.asList(expected).contains(actual));
    }
}
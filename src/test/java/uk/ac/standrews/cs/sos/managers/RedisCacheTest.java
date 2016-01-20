package uk.ac.standrews.cs.sos.managers;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RedisCacheTest {

    private MemCache cache;

    @BeforeMethod
    public void setUp() throws IOException {
        cache = RedisCache.getInstance();
    }

    @AfterMethod
    public void tearDown() {
        cache.flushDB();
        cache.killInstance();
    }

    @Test
    public void testAddSimpleManifest() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        GUID contentGUID = simpleManifestMocked.getContentGUID();

        cache.addManifest(simpleManifestMocked);
        assertEquals(cache.getManifestType(contentGUID), "Atom");
    }

    @Test (expectedExceptions = UnknownManifestTypeException.class)
    public void testAddManifestWithWrongType() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("WrongType");

        cache.addManifest(simpleManifestMocked);
    }

    @Test
    public void testAddAtomManifest() throws Exception {
        AtomManifest atomManifestMocked = mock(AtomManifest.class);

        when(atomManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(atomManifestMocked.getManifestType()).thenReturn("Atom");

        Location locationMocked = new Location("file:///Location_1");
        Location otherLocationMocked = new Location("file:///Location_2");
        Collection<Location> locations = new ArrayList<Location>(
                Arrays.asList(locationMocked, otherLocationMocked)
        );
        when(atomManifestMocked.getLocations()).thenReturn(locations);

        GUID contentGUID = atomManifestMocked.getContentGUID();
        cache.addManifest(atomManifestMocked);

        Collection<Location> cachedLocations = cache.getLocations(contentGUID);
        for(Location location:cachedLocations) {
            assertContains(location, locationMocked, otherLocationMocked);
        }
    }

    @Test
    public void testAddCompoundManifest() throws Exception {
        CompoundManifest compoundManifestMocked = mock(CompoundManifest.class);

        when(compoundManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(compoundManifestMocked.getManifestType()).thenReturn("Compound");
        when(compoundManifestMocked.getSignature()).thenReturn("Test_Signature");

        GUID anyGUID = new GUIDsha1("abc");
        Content contentMocked = new Content("Content_1", anyGUID);
        Content otherContentMocked = new Content("Content_2", anyGUID);
        Collection<Content> contents = new ArrayList<Content>(
                Arrays.asList(contentMocked, otherContentMocked)
        );
        when(compoundManifestMocked.getContents()).thenReturn(contents);

        GUID contentGUID = compoundManifestMocked.getContentGUID();
        cache.addManifest(compoundManifestMocked);
        assertEquals(cache.getSignature(contentGUID), "Test_Signature");

        Collection<Content> cachedContents = cache.getContents(contentGUID);
        for(Content content:cachedContents) {
            assertContains(content, contentMocked, otherContentMocked);
        }
    }

    @Test
    public void testAddAssetManifest() throws Exception {
        AssetManifest assetManifestMocked = mock(AssetManifest.class);

        GUID version = new GUIDsha1("123");
        Content content = new Content(new GUIDsha1("321"));
        GUID invariant = new GUIDsha1("abc123");
        when(assetManifestMocked.getVersionGUID()).thenReturn(version);
        when(assetManifestMocked.getContent()).thenReturn(content);
        when(assetManifestMocked.getContentGUID()).thenReturn(content.getGUID());
        when(assetManifestMocked.getInvariantGUID()).thenReturn(invariant);
        when(assetManifestMocked.getManifestType()).thenReturn("Asset");
        when(assetManifestMocked.getSignature()).thenReturn("Test_Signature");

        GUID metadataFake = new GUIDsha1("abcdef");
        Collection<GUID> metadata = new ArrayList<GUID>(
                Arrays.asList(metadataFake)
        );
        when(assetManifestMocked.getMetadata()).thenReturn(metadata);

        GUID previousOne = new GUIDsha1("prev1");
        GUID previousTwo = new GUIDsha1("prev1");
        Collection<GUID> prevs = new ArrayList<GUID>(
                Arrays.asList(previousOne, previousTwo)
        );
        when(assetManifestMocked.getPreviousManifests()).thenReturn(prevs);

        cache.addManifest(assetManifestMocked);
        assertEquals(cache.getSignature(version), "Test_Signature");

        Collection<GUID> cachedPrevs = cache.getPrevs(version);
        for(GUID prev:cachedPrevs) {
            assertContains(prev, previousOne, previousTwo);
        }

        Collection<GUID> cachedMetadata = cache.getMetadata(version);
        for(GUID meta:cachedMetadata) {
            assertContains(meta, metadataFake);
        }

        assertEquals(cache.getInvariant(version), invariant);

        Collection<Content> contents = cache.getContents(version);
        Iterator<Content> contentIterator = contents.iterator();
        assertEquals(content, contentIterator.next());
    }

    // This must be used, because Redis sets do not preserve ordering
    // http://stackoverflow.com/questions/17801314/how-to-use-or-condition-with-testng-assertions
    private void assertContains(Object actual, Object ... expected) {
        assertTrue(Arrays.asList(expected).contains(actual));
    }
}
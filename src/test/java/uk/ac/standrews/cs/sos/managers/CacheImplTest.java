package uk.ac.standrews.cs.sos.managers;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheImplTest extends CacheBaseTest {

    private final CACHE_TYPE cacheType;
    private static final int DEFAULT_RESULTS = 10;
    private static final int DEFAULT_SKIP_RESULTS = 0;

    @Factory(dataProvider = "cache-manager-provider")
    public CacheImplTest(CACHE_TYPE cacheType) {
        this.cacheType = cacheType;
    }

    @Override
    public CACHE_TYPE getCacheType() {
        return this.cacheType;
    }

    @Test
    public void testAddSimpleManifest() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        cache.addManifest(simpleManifestMocked);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(guids.contains(new GUIDsha1("123")));
    }

    @Test
    public void testAddTwoSameManifests() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        AtomManifest simpleManifestMocked2 = mock(AtomManifest.class);
        when(simpleManifestMocked2.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked2.getManifestType()).thenReturn("Atom");

        cache.addManifest(simpleManifestMocked);
        cache.addManifest(simpleManifestMocked2);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 1);
    }

    @Test
    public void testAddTwoSameManifestsSkipFirst() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        AtomManifest simpleManifestMocked2 = mock(AtomManifest.class);
        when(simpleManifestMocked2.getContentGUID()).thenReturn(new GUIDsha1("456"));
        when(simpleManifestMocked2.getManifestType()).thenReturn("Atom");

        cache.addManifest(simpleManifestMocked);
        cache.addManifest(simpleManifestMocked2);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, 1);
        assertEquals(guids.size(), 1);
    }

    @Test
    public void testAddTwoSameManifestsSkipAll() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        AtomManifest simpleManifestMocked2 = mock(AtomManifest.class);
        when(simpleManifestMocked2.getContentGUID()).thenReturn(new GUIDsha1("456"));
        when(simpleManifestMocked2.getManifestType()).thenReturn("Atom");

        cache.addManifest(simpleManifestMocked);
        cache.addManifest(simpleManifestMocked2);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_RESULTS);
        assertEquals(guids.size(), 0);
    }

    @Test
    public void testAddSimpleCompoundManifest() throws Exception {
        CompoundManifest simpleManifestMocked = mock(CompoundManifest.class);

        Content content = new Content("cat", new GUIDsha1("321"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(content);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Compound");
        when(simpleManifestMocked.getContents()).thenReturn(contents);

        cache.addManifest(simpleManifestMocked);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.COMPOUND, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(guids.contains(new GUIDsha1("123")));

        Collection<GUID> contentGUIDs = cache.getMetaLabelMatches("cat", DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(contentGUIDs.contains(new GUIDsha1("321")));
    }

    @Test
    public void testAddTwoCompoundsWithSharedContent() throws Exception {
        CompoundManifest compoundOne = mock(CompoundManifest.class);

        Content cat = new Content("cat", new GUIDsha1("321"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        when(compoundOne.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(compoundOne.getManifestType()).thenReturn("Compound");
        when(compoundOne.getContents()).thenReturn(contents);

        CompoundManifest compoundTwo = mock(CompoundManifest.class);

        Content anotherCat = new Content("cat", new GUIDsha1("321"));
        Content fish = new Content("fish", new GUIDsha1("678"));
        Collection<Content> otherContents = new ArrayList<>();
        contents.add(fish);
        contents.add(anotherCat);

        when(compoundTwo.getContentGUID()).thenReturn(new GUIDsha1("abc"));
        when(compoundTwo.getManifestType()).thenReturn("Compound");
        when(compoundTwo.getContents()).thenReturn(otherContents);

        cache.addManifest(compoundOne);
        cache.addManifest(compoundTwo);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.COMPOUND, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<GUID> contentGUIDs = cache.getMetaLabelMatches("cat", DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(contentGUIDs.size(), 1);
    }

    // Test that the index is persisted.
    @Test
    public void testDataIsDumped() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        cache.addManifest(simpleManifestMocked);

        // Kill this instance, so that next instance used the dumped file.
        SeaConfiguration configuration = cache.getConfiguration();
        cache.killInstance();
        cache = new CacheFactory().getCache(cacheType, configuration);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(guids.contains(new GUIDsha1("123")));
    }

    @Test
    public void testAddAssetManifest() throws Exception {
        AssetManifest assetManifestMocked = mock(AssetManifest.class);

        GUID version = new GUIDsha1("123");
        GUID invariant = new GUIDsha1("abc123");
        when(assetManifestMocked.getVersionGUID()).thenReturn(version);
        when(assetManifestMocked.getInvariantGUID()).thenReturn(invariant);
        when(assetManifestMocked.getManifestType()).thenReturn("Asset");

        cache.addManifest(assetManifestMocked);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 1);
        assertTrue(guids.contains(new GUIDsha1("abc123")));
    }

    @Test
    public void testAddMultipleAssetManifests() throws Exception {
        AssetManifest firstAsset = createMockedManifest(new GUIDsha1("123"), new GUIDsha1("abc123"));
        AssetManifest secondAsset = createMockedManifest(new GUIDsha1("456"), new GUIDsha1("abc123"));
        AssetManifest thirdAsset = createMockedManifest(new GUIDsha1("123456"), new GUIDsha1("hgi678"));
        // Cannot two assets with same version GUID and different incarnation.
        AssetManifest invalidAsset = createMockedManifest(new GUIDsha1("123"), new GUIDsha1("def123"));

        cache.addManifest(firstAsset);
        cache.addManifest(secondAsset);
        cache.addManifest(thirdAsset);
        cache.addManifest(invalidAsset);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<GUID> versions = cache.getVersions(new GUIDsha1("abc123"), DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(versions.size(), 2);
        assertTrue(versions.contains(new GUIDsha1("123")));
        assertTrue(versions.contains(new GUIDsha1("456")));
    }

    @Test
    public void testAddMultipleAssetManifestsAndSkipFirstResult() throws Exception {
        AssetManifest firstAsset = createMockedManifest(new GUIDsha1("123"), new GUIDsha1("abc123"));
        AssetManifest secondAsset = createMockedManifest(new GUIDsha1("456"), new GUIDsha1("abc123"));
        AssetManifest thirdAsset = createMockedManifest(new GUIDsha1("123456"), new GUIDsha1("hgi678"));
        // Cannot two assets with same version GUID and different incarnation.
        AssetManifest invalidAsset = createMockedManifest(new GUIDsha1("123"), new GUIDsha1("def123"));

        cache.addManifest(firstAsset);
        cache.addManifest(secondAsset);
        cache.addManifest(thirdAsset);
        cache.addManifest(invalidAsset);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<GUID> versions = cache.getVersions(new GUIDsha1("abc123"), DEFAULT_RESULTS, 1);
        assertEquals(versions.size(), 1);
        assertTrue(versions.contains(new GUIDsha1("456")));
    }

    @Test
    public void testAddMultipleAssetManifestsAndSkipAllResults() throws Exception {
        AssetManifest firstAsset = createMockedManifest(new GUIDsha1("123"), new GUIDsha1("abc123"));
        AssetManifest secondAsset = createMockedManifest(new GUIDsha1("456"), new GUIDsha1("abc123"));
        AssetManifest thirdAsset = createMockedManifest(new GUIDsha1("123456"), new GUIDsha1("hgi678"));
        // Cannot two assets with same version GUID and different incarnation.
        AssetManifest invalidAsset = createMockedManifest(new GUIDsha1("123"), new GUIDsha1("def123"));

        cache.addManifest(firstAsset);
        cache.addManifest(secondAsset);
        cache.addManifest(thirdAsset);
        cache.addManifest(invalidAsset);

        Collection<GUID> guids = cache.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<GUID> versions = cache.getVersions(new GUIDsha1("abc123"), DEFAULT_RESULTS, DEFAULT_RESULTS);
        assertEquals(versions.size(), 0);
    }

    private AssetManifest createMockedManifest(GUID version, GUID invariant) {
        AssetManifest assetManifestMocked = mock(AssetManifest.class);

        when(assetManifestMocked.getVersionGUID()).thenReturn(version);
        when(assetManifestMocked.getInvariantGUID()).thenReturn(invariant);
        when(assetManifestMocked.getManifestType()).thenReturn("Asset");

        return assetManifestMocked;
    }
}

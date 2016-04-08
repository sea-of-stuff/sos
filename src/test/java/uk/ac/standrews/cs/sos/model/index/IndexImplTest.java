package uk.ac.standrews.cs.sos.model.index;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.manifests.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IndexImplTest extends IndexBaseTest {

    private final CACHE_TYPE cacheType;
    private static final int DEFAULT_RESULTS = 10;
    private static final int DEFAULT_SKIP_RESULTS = 0;

    @Factory(dataProvider = "index-manager-provider")
    public IndexImplTest(CACHE_TYPE cacheType) {
        this.cacheType = cacheType;
    }

    @Override
    public CACHE_TYPE getCacheType() {
        return this.cacheType;
    }

    @Test
    public void testAddSimpleManifest() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        index.addManifest(simpleManifestMocked);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(guids.contains(GUIDFactory.recreateGUID("123")));
    }

    @Test
    public void testAddTwoSameManifests() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        AtomManifest simpleManifestMocked2 = mock(AtomManifest.class);
        when(simpleManifestMocked2.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked2.getManifestType()).thenReturn("Atom");

        index.addManifest(simpleManifestMocked);
        index.addManifest(simpleManifestMocked2);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 1);
    }

    @Test
    public void testAddTwoSameManifestsSkipFirst() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        AtomManifest simpleManifestMocked2 = mock(AtomManifest.class);
        when(simpleManifestMocked2.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("456"));
        when(simpleManifestMocked2.getManifestType()).thenReturn("Atom");

        index.addManifest(simpleManifestMocked);
        index.addManifest(simpleManifestMocked2);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, 1);
        assertEquals(guids.size(), 1);
    }

    @Test
    public void testAddTwoSameManifestsSkipAll() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);
        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        AtomManifest simpleManifestMocked2 = mock(AtomManifest.class);
        when(simpleManifestMocked2.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("456"));
        when(simpleManifestMocked2.getManifestType()).thenReturn("Atom");

        index.addManifest(simpleManifestMocked);
        index.addManifest(simpleManifestMocked2);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_RESULTS);
        assertEquals(guids.size(), 0);
    }

    @Test
    public void testAddSimpleCompoundManifest() throws Exception {
        CompoundManifest simpleManifestMocked = mock(CompoundManifest.class);

        Content content = new Content("cat", GUIDFactory.recreateGUID("321"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(content);

        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Compound");
        when(simpleManifestMocked.getContents()).thenReturn(contents);

        index.addManifest(simpleManifestMocked);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.COMPOUND, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(guids.contains(GUIDFactory.recreateGUID("123")));

        Collection<IGUID> contentGUIDs = index.getMetaLabelMatches("cat", DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(contentGUIDs.contains(GUIDFactory.recreateGUID("321")));
    }

    @Test
    public void testAddSimpleCompoundManifestSkipResult() throws Exception {
        CompoundManifest simpleManifestMocked = mock(CompoundManifest.class);

        Content content = new Content("cat", GUIDFactory.recreateGUID("321"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(content);

        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Compound");
        when(simpleManifestMocked.getContents()).thenReturn(contents);

        index.addManifest(simpleManifestMocked);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.COMPOUND, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(guids.contains(GUIDFactory.recreateGUID("123")));

        Collection<IGUID> contentGUIDs = index.getMetaLabelMatches("cat", DEFAULT_RESULTS, 1);
        assertEquals(contentGUIDs.size(), 0);
    }

    @Test
    public void testAddTwoCompoundsWithSharedContent() throws Exception {
        CompoundManifest compoundOne = mock(CompoundManifest.class);

        Content cat = new Content("cat", GUIDFactory.recreateGUID("321"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        when(compoundOne.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(compoundOne.getManifestType()).thenReturn("Compound");
        when(compoundOne.getContents()).thenReturn(contents);

        CompoundManifest compoundTwo = mock(CompoundManifest.class);

        Content anotherCat = new Content("cat", GUIDFactory.recreateGUID("321"));
        Content fish = new Content("fish", GUIDFactory.recreateGUID("678"));
        Collection<Content> otherContents = new ArrayList<>();
        contents.add(fish);
        contents.add(anotherCat);

        when(compoundTwo.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("abc"));
        when(compoundTwo.getManifestType()).thenReturn("Compound");
        when(compoundTwo.getContents()).thenReturn(otherContents);

        index.addManifest(compoundOne);
        index.addManifest(compoundTwo);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.COMPOUND, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<IGUID> contentGUIDs = index.getMetaLabelMatches("cat", DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(contentGUIDs.size(), 1);
    }

    // Test that the index is persisted.
    @Test
    public void testDataIsDumped() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("Atom");

        index.addManifest(simpleManifestMocked);

        // Kill this instance, so that next instance used the dumped file.
        SeaConfiguration configuration = index.getConfiguration();
        index.killInstance();
        index = new CacheFactory().getCache(cacheType, configuration);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ATOM, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertTrue(guids.contains(GUIDFactory.recreateGUID("123")));
    }

    @Test
    public void testAddAssetManifest() throws Exception {
        AssetManifest assetManifestMocked = mock(AssetManifest.class);

        IGUID version = GUIDFactory.recreateGUID("123");
        IGUID invariant = GUIDFactory.recreateGUID("abc123");
        when(assetManifestMocked.getVersionGUID()).thenReturn(version);
        when(assetManifestMocked.getInvariantGUID()).thenReturn(invariant);
        when(assetManifestMocked.getManifestType()).thenReturn("Asset");

        index.addManifest(assetManifestMocked);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 1);
        assertTrue(guids.contains(GUIDFactory.recreateGUID("abc123")));
    }

    @Test
    public void testAddMultipleAssetManifests() throws Exception {
        AssetManifest firstAsset = createMockedManifest(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("abc123"));
        AssetManifest secondAsset = createMockedManifest(GUIDFactory.recreateGUID("456"), GUIDFactory.recreateGUID("abc123"));
        AssetManifest thirdAsset = createMockedManifest(GUIDFactory.recreateGUID("123456"), GUIDFactory.recreateGUID("def678"));
        // Cannot have two assets with same version GUID and different incarnation.
        AssetManifest invalidAsset = createMockedManifest(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("def123"));

        index.addManifest(firstAsset);
        index.addManifest(secondAsset);
        index.addManifest(thirdAsset);
        index.addManifest(invalidAsset);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<IGUID> versions = index.getVersions(GUIDFactory.recreateGUID("abc123"), DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(versions.size(), 2);
        assertTrue(versions.contains(GUIDFactory.recreateGUID("123")));
        assertTrue(versions.contains(GUIDFactory.recreateGUID("456")));
    }

    @Test
    public void testAddMultipleAssetManifestsAndSkipFirstResult() throws Exception {
        AssetManifest firstAsset = createMockedManifest(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("abc123"));
        AssetManifest secondAsset = createMockedManifest(GUIDFactory.recreateGUID("456"), GUIDFactory.recreateGUID("abc123"));
        AssetManifest thirdAsset = createMockedManifest(GUIDFactory.recreateGUID("123456"), GUIDFactory.recreateGUID("def678"));
        // Cannot have two assets with same version GUID and different incarnation.
        AssetManifest invalidAsset = createMockedManifest(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("def123"));

        index.addManifest(firstAsset);
        index.addManifest(secondAsset);
        index.addManifest(thirdAsset);
        index.addManifest(invalidAsset);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<IGUID> versions = index.getVersions(GUIDFactory.recreateGUID("abc123"), DEFAULT_RESULTS, 1);
        assertEquals(versions.size(), 1);
        assertTrue(versions.contains(GUIDFactory.recreateGUID("456")));
    }

    @Test
    public void testAddMultipleAssetManifestsAndSkipAllResults() throws Exception {
        AssetManifest firstAsset = createMockedManifest(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("abc123"));
        AssetManifest secondAsset = createMockedManifest(GUIDFactory.recreateGUID("456"), GUIDFactory.recreateGUID("abc123"));
        AssetManifest thirdAsset = createMockedManifest(GUIDFactory.recreateGUID("123456"), GUIDFactory.recreateGUID("def678"));
        // Cannot have two assets with same version GUID and different incarnation.
        AssetManifest invalidAsset = createMockedManifest(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("def123"));

        index.addManifest(firstAsset);
        index.addManifest(secondAsset);
        index.addManifest(thirdAsset);
        index.addManifest(invalidAsset);

        Collection<IGUID> guids = index.getManifestsOfType(ManifestConstants.ASSET, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        assertEquals(guids.size(), 2);

        Collection<IGUID> versions = index.getVersions(GUIDFactory.recreateGUID("abc123"), DEFAULT_RESULTS, DEFAULT_RESULTS);
        assertEquals(versions.size(), 0);
    }

    private AssetManifest createMockedManifest(IGUID version, IGUID invariant) {
        AssetManifest assetManifestMocked = mock(AssetManifest.class);

        when(assetManifestMocked.getVersionGUID()).thenReturn(version);
        when(assetManifestMocked.getInvariantGUID()).thenReturn(invariant);
        when(assetManifestMocked.getManifestType()).thenReturn("Asset");

        return assetManifestMocked;
    }
}

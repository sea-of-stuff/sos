package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestCacheImpl_HEADS_CURRENT_Test extends CommonTest {

    @Test
    public void basicTest() throws Exception {
        ManifestsCache cache = new ManifestsCacheImpl();

        Version version = ManifestUtils.createDummyVersion();
        cache.advanceHead(version.getInvariantGUID(), version.guid());

        Set<IGUID> heads = cache.getHeads(version.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);
        assertTrue(heads.contains(version.guid()));
    }

    @Test
    public void basicHeadTest() throws Exception {
        ManifestsCache cache = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        IGUID guid = versionManifest.getVersionGUID();

        cache.advanceHead(versionManifest.getInvariantGUID(), guid);

        Set<IGUID> heads = cache.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        Assert.assertTrue(heads.contains(guid));
    }

    @Test
    public void advanceHeadTest() throws Exception {
        ManifestsCache cache = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        cache.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("456"), Collections.singleton(versionManifest.guid()), versionManifest.getInvariantGUID());

        cache.advanceHead(newVersionManifest.getInvariantGUID(), Collections.singleton(versionManifest.guid()), newVersionManifest.guid());

        Set<IGUID> heads = cache.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        Assert.assertTrue(heads.contains(newVersionManifest.guid()));
    }

    @Test
    public void multipleHeadsTest() throws Exception {

        ManifestsCache cache = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion();

        cache.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());
        cache.advanceHead(versionManifest.getInvariantGUID(), siblingVersionManifest.guid());

        Set<IGUID> heads = cache.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 2);

        Assert.assertTrue(heads.contains(versionManifest.guid()));
        Assert.assertTrue(heads.contains(siblingVersionManifest.guid()));
    }

    @Test
    public void advanceMultipleHeadsTest() throws Exception {

        ManifestsCache cache = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion();

        cache.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());
        cache.advanceHead(versionManifest.getInvariantGUID(), siblingVersionManifest.guid());

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("789"),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.getInvariantGUID());

        cache.advanceHead(newVersionManifest.getInvariantGUID(), new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())), newVersionManifest.guid());

        Set<IGUID> heads = cache.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        Assert.assertTrue(heads.contains(newVersionManifest.guid()));
    }


}


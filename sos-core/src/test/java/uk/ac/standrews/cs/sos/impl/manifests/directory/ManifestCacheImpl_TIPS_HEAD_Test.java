package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestCacheImpl_TIPS_HEAD_Test {

    @Test
    public void basicTipTest() throws Exception {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceTip(versionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.getVersionGUID()));
    }

    @Test
    public void advanceTipTest() throws Exception {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceTip(versionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), Collections.singleton(versionManifest.guid()), versionManifest.getInvariantGUID());

        directory.advanceTip(newVersionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    @Test
    public void multipleTipsTest() throws Exception {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.getInvariantGUID());

        directory.advanceTip(versionManifest);
        directory.advanceTip(siblingVersionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 2);

        assertTrue(tips.contains(versionManifest.guid()));
        assertTrue(tips.contains(siblingVersionManifest.guid()));
    }

    @Test
    public void advanceMultipleTipsTest() throws Exception {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.getInvariantGUID());

        directory.advanceTip(versionManifest);
        directory.advanceTip(siblingVersionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.getInvariantGUID());

        directory.advanceTip(newVersionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    @Test
    public void basicHeadTest() throws Exception, HEADNotFoundException {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();

        directory.setHead(versionManifest);

        IGUID head = directory.getHead(versionManifest.getInvariantGUID());
        assertEquals(head, versionManifest.guid());
    }

    @Test
    public void basicOnlyOneHeadSameVersionTest() throws Exception, HEADNotFoundException {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();

        directory.setHead(versionManifest);
        directory.setHead(versionManifest);

        IGUID head = directory.getHead(versionManifest.getInvariantGUID());
        assertEquals(head, versionManifest.guid());
    }

    @Test
    public void basicMultiHeadDifferentVersionTest() throws Exception, HEADNotFoundException {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version otherVersionManifest = ManifestUtils.createDummyVersion();

        directory.setHead(versionManifest);
        directory.setHead(otherVersionManifest);

        IGUID head = directory.getHead(versionManifest.getInvariantGUID());
        assertEquals(head, versionManifest.guid());

        IGUID otherHead = directory.getHead(otherVersionManifest.getInvariantGUID());
        assertEquals(otherHead, otherVersionManifest.guid());
    }

    @Test
    public void noDuplicatesInTip() throws Exception, HEADNotFoundException {
        ManifestsCache directory = new ManifestsCacheImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceTip(versionManifest);
        directory.advanceTip(versionManifest);
        directory.advanceTip(versionManifest);
        directory.advanceTip(versionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.getVersionGUID()));
    }

}

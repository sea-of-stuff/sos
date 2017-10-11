package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsIndex;
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
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.advanceTip(versionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.version()));
    }

    @Test
    public void advanceTipTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.advanceTip(versionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), Collections.singleton(versionManifest.guid()), versionManifest.invariant());

        index.advanceTip(newVersionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    @Test
    public void multipleTipsTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.invariant());

        index.advanceTip(versionManifest);
        index.advanceTip(siblingVersionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 2);

        assertTrue(tips.contains(versionManifest.guid()));
        assertTrue(tips.contains(siblingVersionManifest.guid()));
    }

    @Test
    public void advanceMultipleTipsTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.invariant());

        index.advanceTip(versionManifest);
        index.advanceTip(siblingVersionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.invariant());

        index.advanceTip(newVersionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    @Test
    public void basicHeadTest() throws Exception, HEADNotFoundException {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();

        index.setHead(versionManifest);

        IGUID head = index.getHead(versionManifest.invariant());
        assertEquals(head, versionManifest.guid());
    }

    @Test
    public void basicOnlyOneHeadSameVersionTest() throws Exception, HEADNotFoundException {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();

        index.setHead(versionManifest);
        index.setHead(versionManifest);

        IGUID head = index.getHead(versionManifest.invariant());
        assertEquals(head, versionManifest.guid());
    }

    @Test
    public void basicMultiHeadDifferentVersionTest() throws Exception, HEADNotFoundException {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version otherVersionManifest = ManifestUtils.createDummyVersion();

        index.setHead(versionManifest);
        index.setHead(otherVersionManifest);

        IGUID head = index.getHead(versionManifest.invariant());
        assertEquals(head, versionManifest.guid());

        IGUID otherHead = index.getHead(otherVersionManifest.invariant());
        assertEquals(otherHead, otherVersionManifest.guid());
    }

    @Test
    public void noDuplicatesInTip() throws Exception, HEADNotFoundException {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.advanceTip(versionManifest);
        index.advanceTip(versionManifest);
        index.advanceTip(versionManifest);
        index.advanceTip(versionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.version()));
    }

}

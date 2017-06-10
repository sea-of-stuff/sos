package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
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
public class HEADS_CURRENT_Test extends CommonTest {
    
    public void basicHeadTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        IGUID guid = versionManifest.getVersionGUID();
        directory.advanceHead(versionManifest.getInvariantGUID(), guid);

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(guid));
    }
    
    public void advanceHeadTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("456"), Collections.singleton(versionManifest.guid()), versionManifest.getInvariantGUID());

        directory.advanceHead(newVersionManifest.getInvariantGUID(), Collections.singleton(versionManifest.guid()), newVersionManifest.guid());

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(newVersionManifest.guid()));
    }

    public void multipleHeadsTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion();

        directory.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());
        directory.advanceHead(versionManifest.getInvariantGUID(), siblingVersionManifest.guid());

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 2);

        assertTrue(heads.contains(versionManifest.guid()));
        assertTrue(heads.contains(siblingVersionManifest.guid()));
    }

    public void advanceMultipleHeadsTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion();

        directory.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());
        directory.advanceHead(versionManifest.getInvariantGUID(), siblingVersionManifest.guid());

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("789"),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.getInvariantGUID());

        directory.advanceHead(newVersionManifest.getInvariantGUID(), new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())), newVersionManifest.guid());

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(newVersionManifest.guid()));
    }

}

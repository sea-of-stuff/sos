package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
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
public class LocalManifestsDirectory_HEADS_CURRENT_Test extends LocalManifestsDirectoryTest {

    @Test
    public void basicHeadTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(storage);

        Version versionManifest = ManifestUtils.createDummyVersion();
        IGUID guid = versionManifest.getVersionGUID();
        manifestsDirectory.advanceHead(versionManifest.getInvariantGUID(), guid);

        Set<IGUID> heads = manifestsDirectory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(guid));
    }

    @Test
    public void advanceHeadTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(storage);

        Version versionManifest = ManifestUtils.createDummyVersion();
        manifestsDirectory.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("456"), Collections.singleton(versionManifest.guid()), versionManifest.getInvariantGUID());

        manifestsDirectory.advanceHead(newVersionManifest.getInvariantGUID(), Collections.singleton(versionManifest.guid()), newVersionManifest.guid());

        Set<IGUID> heads = manifestsDirectory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(newVersionManifest.guid()));
    }

    @Test
    public void multipleHeadsTest() throws Exception {

        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(storage);

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion();

        manifestsDirectory.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());
        manifestsDirectory.advanceHead(versionManifest.getInvariantGUID(), siblingVersionManifest.guid());

        Set<IGUID> heads = manifestsDirectory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 2);

        assertTrue(heads.contains(versionManifest.guid()));
        assertTrue(heads.contains(siblingVersionManifest.guid()));
    }

    @Test
    public void advanceMultipleHeadsTest() throws Exception {

        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(storage);

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion();

        manifestsDirectory.advanceHead(versionManifest.getInvariantGUID(), versionManifest.guid());
        manifestsDirectory.advanceHead(versionManifest.getInvariantGUID(), siblingVersionManifest.guid());

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("789"),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.getInvariantGUID());

        manifestsDirectory.advanceHead(newVersionManifest.getInvariantGUID(), new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())), newVersionManifest.guid());

        Set<IGUID> heads = manifestsDirectory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(newVersionManifest.guid()));
    }

}
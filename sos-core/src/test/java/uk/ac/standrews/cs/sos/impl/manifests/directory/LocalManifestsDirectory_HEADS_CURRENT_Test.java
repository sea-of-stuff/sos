package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectory_HEADS_CURRENT_Test extends LocalManifestsDirectoryTest {

    @Test
    public void basicHeadTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(storage);

        IGUID contentGUID = GUIDFactory.recreateGUID("123");
        Version versionManifest = createDummyVersion(contentGUID);

        IGUID guid = versionManifest.getVersionGUID();

        manifestsDirectory.advanceHead(versionManifest.getInvariantGUID(), guid);

        Set<IGUID> heads = manifestsDirectory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(guid));
    }

    @Test
    public void advanceHeadTest() {

    }

    @Test
    public void advanceMultipleHeadsTest() {

    }


    private Version createDummyVersion(IGUID contentGUID) throws Exception {
        Role roleMocked = mock(Role.class);
        when(roleMocked.sign(any(String.class))).thenReturn("AAAB");
        when(roleMocked.guid()).thenReturn(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED));
        Version version = ManifestFactory.createVersionManifest(contentGUID, null, null, null, roleMocked);

        return version;
    }

}
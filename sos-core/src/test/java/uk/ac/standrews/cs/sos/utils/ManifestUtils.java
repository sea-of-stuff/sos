package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestUtils {

    public static Version createDummyVersion() throws Exception {
        Role roleMocked = mock(Role.class);
        when(roleMocked.sign(any(String.class))).thenReturn("AAAB");
        when(roleMocked.guid()).thenReturn(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED));
        Version version = ManifestFactory.createVersionManifest(GUIDFactory.generateRandomGUID(), null, null, null, roleMocked);

        return version;
    }

    public static Version createDummyVersion(IGUID contentGUID) throws Exception {
        Role roleMocked = mock(Role.class);
        when(roleMocked.sign(any(String.class))).thenReturn("AAAB");
        when(roleMocked.guid()).thenReturn(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED));
        Version version = ManifestFactory.createVersionManifest(contentGUID, null, null, null, roleMocked);

        return version;
    }

    public static Version createDummyVersion(IGUID contentGUID, Set<IGUID> previous, IGUID invariant) throws Exception {
        Role roleMocked = mock(Role.class);
        when(roleMocked.sign(any(String.class))).thenReturn("AAAB");
        when(roleMocked.guid()).thenReturn(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED));
        Version version = ManifestFactory.createVersionManifest(contentGUID, invariant, previous, null, roleMocked);

        return version;
    }

    public static Version createDummyVersion(IGUID contentGUID, IGUID invariant) throws Exception {
        Role roleMocked = mock(Role.class);
        when(roleMocked.sign(any(String.class))).thenReturn("AAAB");
        when(roleMocked.guid()).thenReturn(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED));
        Version version = ManifestFactory.createVersionManifest(contentGUID, invariant, null, null, roleMocked);

        return version;
    }

    public static Manifest createMockManifestTypeAtom() {
        Manifest manifest = mock(Manifest.class);
        IGUID guid = GUIDFactory.generateRandomGUID();
        when(manifest.guid()).thenReturn(guid);
        when(manifest.guid()).thenReturn(guid);
        when(manifest.isValid()).thenReturn(true);
        when(manifest.getType()).thenReturn(ManifestType.ATOM);

        return manifest;
    }
}

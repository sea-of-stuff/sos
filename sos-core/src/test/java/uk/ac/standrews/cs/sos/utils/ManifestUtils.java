package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.*;

import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestUtils {

    public static Version createDummyVersion() throws Exception {
        Role roleMocked = UserRoleUtils.BareRoleMock();
        Version version = ManifestFactory.createVersionManifest(GUIDFactory.generateRandomGUID(), null, null, null, roleMocked);

        return version;
    }

    public static Version createDummyVersion(IGUID contentGUID) throws Exception {
        Role roleMocked = UserRoleUtils.BareRoleMock();
        Version version = ManifestFactory.createVersionManifest(contentGUID, null, null, null, roleMocked);

        return version;
    }

    public static Version createDummyVersion(IGUID contentGUID, Set<IGUID> previous, IGUID invariant) throws Exception {
        Role roleMocked = UserRoleUtils.BareRoleMock();
        Version version = ManifestFactory.createVersionManifest(contentGUID, invariant, previous, null, roleMocked);

        return version;
    }

    public static Version createDummyVersion(IGUID contentGUID, IGUID invariant) throws Exception {
        Role roleMocked = UserRoleUtils.BareRoleMock();
        Version version = ManifestFactory.createVersionManifest(contentGUID, invariant, null, null, roleMocked);

        return version;
    }

    public static Manifest createMockManifestTypeAtom() {
        Manifest manifest = mock(Manifest.class);
        IGUID guid = GUIDFactory.generateRandomGUID();
        when(manifest.guid()).thenReturn(guid);
        when(manifest.isValid()).thenReturn(true);
        when(manifest.getType()).thenReturn(ManifestType.ATOM);

        return manifest;
    }

    public static Manifest createMockAtom() {
        return createMockAtom(GUIDFactory.generateRandomGUID());
    }

    public static Manifest createMockAtom(IGUID atomGUID) {
        Atom manifest = mock(Atom.class);
        IGUID guid = atomGUID;
        when(manifest.guid()).thenReturn(guid);
        when(manifest.isValid()).thenReturn(true);
        when(manifest.getType()).thenReturn(ManifestType.ATOM);

        Set<LocationBundle> bundles = new LinkedHashSet<>();
        Location location = null;
        try {
            location = new SOSLocation(GUIDFactory.generateRandomGUID(), GUIDFactory.generateRandomGUID());
            bundles.add(new CacheLocationBundle(location));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        when(manifest.getLocations()).thenReturn(bundles);

        return manifest;
    }
}

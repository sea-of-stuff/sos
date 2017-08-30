package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;

import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Set;

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

        IGUID guid = GUIDFactory.generateRandomGUID();
        return new AtomManifest(guid, new LinkedHashSet<>());
    }

    public static Manifest createMockAtom() {
        return createMockAtom(GUIDFactory.generateRandomGUID());
    }

    public static Manifest createMockAtom(IGUID atomGUID) {

        Set<LocationBundle> bundles = new LinkedHashSet<>();
        Location location;
        try {
            location = new SOSLocation(GUIDFactory.generateRandomGUID(), GUIDFactory.generateRandomGUID());
            bundles.add(new CacheLocationBundle(location));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return new AtomManifest(atomGUID, bundles);
    }
}

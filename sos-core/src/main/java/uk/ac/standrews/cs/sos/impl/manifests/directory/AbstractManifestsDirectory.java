package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.SecureManifest;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractManifestsDirectory implements ManifestsDirectory {

    abstract void advanceTip(IGUID invariant, IGUID version);
    abstract void advanceTip(IGUID invariant, Set<IGUID> previousVersions, IGUID newVersion);

    @Override
    public void advanceTip(Version version) {

        Set<IGUID> previousVersions = version.getPreviousVersions();

        if (previousVersions == null || previousVersions.isEmpty()) {
            advanceTip(version.getInvariantGUID(), version.guid());
        } else {
            advanceTip(version.getInvariantGUID(), version.getPreviousVersions(), version.guid());
        }

    }

    protected Manifest mergeManifests(IGUID guid, Atom first, Atom second) {
        Set<LocationBundle> locations = new TreeSet<>(LocationsIndexImpl.comparator());

        locations.addAll(first.getLocations());
        locations.addAll(second.getLocations());

        return ManifestFactory.createAtomManifest(guid, locations);
    }

    protected HashMap<IGUID, String> mergeSecureManifestsKeys(SecureManifest first, SecureManifest second) {
        HashMap<IGUID, String> keysRoles = new LinkedHashMap<>();

        keysRoles.putAll(first.keysRoles());
        keysRoles.putAll(second.keysRoles());

        return keysRoles;
    }
}

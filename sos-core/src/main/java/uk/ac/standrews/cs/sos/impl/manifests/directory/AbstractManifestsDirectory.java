package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.SecureAtom;

import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractManifestsDirectory implements ManifestsDirectory {

    protected Manifest mergeManifests(IGUID guid, Atom first, Atom second) {
        Set<LocationBundle> locations = new TreeSet<>(LocationsIndexImpl.comparator());

        locations.addAll(first.getLocations());
        locations.addAll(second.getLocations());

        return ManifestFactory.createAtomManifest(guid, locations);
    }

    protected Manifest mergeManifests(IGUID guid, SecureAtom first, SecureAtom second) throws ManifestNotMadeException {
        Set<LocationBundle> locations = new TreeSet<>(LocationsIndexImpl.comparator());

        locations.addAll(first.getLocations());
        locations.addAll(second.getLocations());

        HashMap<IGUID, String> rolesToKeys = new LinkedHashMap<>(first.keysRoles());
        for(Map.Entry<IGUID, String> rtk:second.keysRoles().entrySet()) {

            if (!rolesToKeys.containsKey(rtk.getKey())) rolesToKeys.put(rtk.getKey(), rtk.getValue());
        }

        return ManifestFactory.createSecureAtomManifest(guid, locations, rolesToKeys);
    }

}

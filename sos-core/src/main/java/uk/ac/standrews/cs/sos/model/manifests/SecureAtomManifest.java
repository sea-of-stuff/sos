package uk.ac.standrews.cs.sos.model.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifest extends AtomManifest {

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid
     * @param locations
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(guid, locations);
    }

    private void encrypt() {
        // Generate random key K
        // encrypt data with K --> d'
        // encrypt k with pubkey --> (k', pubkey)
        // guid = hash(d')
    }
}

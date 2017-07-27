package uk.ac.standrews.cs.sos.impl.manifests.atom.store;

import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;

/**
 * Classes implementing this interface should store data and return the
 * information as specified.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Store {

    IGUID store() throws StorageException;

    LocationBundle getLocationBundle();
}

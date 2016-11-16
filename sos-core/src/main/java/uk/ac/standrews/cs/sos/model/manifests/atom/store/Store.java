package uk.ac.standrews.cs.sos.model.manifests.atom.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

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

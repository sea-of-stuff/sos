package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Store {

    IGUID store() throws StorageException;

    LocationBundle getLocationBundle();
}

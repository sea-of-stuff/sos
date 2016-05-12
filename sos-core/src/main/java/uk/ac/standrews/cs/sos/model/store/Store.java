package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Store {

    IGUID store() throws DataStorageException;

    LocationBundle getLocationBundle();
}

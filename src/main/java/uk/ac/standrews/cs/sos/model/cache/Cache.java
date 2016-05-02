package uk.ac.standrews.cs.sos.model.cache;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Cache {

    IGUID cache() throws DataStorageException;

    CacheLocationBundle getCacheLocationBundle();
}

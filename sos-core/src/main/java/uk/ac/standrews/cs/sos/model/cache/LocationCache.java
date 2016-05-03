package uk.ac.standrews.cs.sos.model.cache;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.storage.DataStorageHelper;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationCache extends CommonCache implements Cache {

    private Location origin;
    private CacheLocationBundle cacheLocationBundle;

    public LocationCache(SeaConfiguration configuration, Location location) {
        super(configuration);
        this.origin = location;
    }

    @Override
    public IGUID cache() throws DataStorageException {
        IGUID guid;
        if (origin == null) {
            throw new DataStorageException();
        }

        try {
            guid = generateGUID(origin);
            if (guid == null) {
                return null;
            }

            storeData(origin, guid);
            cacheLocationBundle = getCacheBundle(guid);
        } catch (GUIDGenerationException | SourceLocationException e) {
            throw new DataStorageException();
        }

        return guid;
    }

    @Override
    public CacheLocationBundle getCacheLocationBundle() {
        return cacheLocationBundle;
    }

    private void storeData(Location location, IGUID guid) throws DataStorageException {
        try {
            InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);
            storeData(dataStream, guid);
        } catch (SourceLocationException e) {
            throw new DataStorageException();
        }
    }


}

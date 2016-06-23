package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.storage.DataStorageHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class LocationStore extends CommonStore {

    private Location origin;
    private LocationBundle locationBundle;

    public LocationStore(Configuration configuration, Location location) {
        super(configuration);
        this.origin = location;
    }

    @Override
    public IGUID store() throws DataStorageException {
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
            Location location = getLocation(guid);
            locationBundle = getBundle(location);
        } catch (GUIDGenerationException | SourceLocationException e) {
            throw new DataStorageException();
        }

        return guid;
    }

    @Override
    public LocationBundle getLocationBundle() {
        return locationBundle;
    }

    private void storeData(Location location, IGUID guid) throws DataStorageException {

        try (InputStream dataStream =
                     DataStorageHelper.getInputStreamFromLocation(location)){
            storeData(dataStream, guid);
        } catch (SourceLocationException | IOException e) {
            throw new DataStorageException();
        }
    }

}

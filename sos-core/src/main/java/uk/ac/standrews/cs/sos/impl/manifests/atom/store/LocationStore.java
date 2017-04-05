package uk.ac.standrews.cs.sos.impl.manifests.atom.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.storage.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.storage.data.InputStreamData;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class LocationStore extends CommonLocalStore {

    private Location origin;
    private LocationBundle locationBundle;

    public LocationStore(IGUID nodeGUID, LocalStorage storage, Location location) {
        super(nodeGUID, storage);
        this.origin = location;
    }

    @Override
    public IGUID store() throws StorageException {
        IGUID guid;
        if (origin == null) {
            throw new StorageException();
        }

        try {
            guid = generateGUID(origin);
            if (guid == null || guid.isInvalid()) {
                return null;
            }

            // TODO - do not store if data is already in disk

            storeData(origin, guid);
            Location location = getLocation(guid);
            locationBundle = getBundle(location);
        } catch (GUIDGenerationException | SourceLocationException e) {
            throw new StorageException();
        }

        return guid;
    }

    @Override
    public LocationBundle getLocationBundle() {
        return locationBundle;
    }

    private void storeData(Location location, IGUID guid) throws StorageException {

        try (InputStream dataStream =
                     LocationUtility.getInputStreamFromLocation(location)) {

            InputStreamData data = new InputStreamData(dataStream);
            storeData(guid, data);
        } catch (DataStorageException | IOException e) {
            throw new StorageException();
        }
    }

}

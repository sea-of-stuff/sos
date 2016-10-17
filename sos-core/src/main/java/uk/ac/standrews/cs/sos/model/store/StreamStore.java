package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.storage.InternalStorage;
import uk.ac.standrews.cs.sos.utils.FileHelper;
import uk.ac.standrews.cs.storage.data.InputStreamData;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class StreamStore extends CommonLocalStore {

    private InputStream inputStream;
    private LocationBundle locationBundle;

    public StreamStore(IGUID nodeGUID, InternalStorage storage, InputStream inputStream) {
        super(nodeGUID, storage);
        this.inputStream = inputStream;
    }

    @Override
    public IGUID store() throws StorageException {
            IGUID guid;
            if (inputStream == null) {
                throw new StorageException();
            }

            // TODO - this code could be improved a lot, by not opening stream twice!
            try {
                IGUID tmpGUID = GUIDFactory.generateRandomGUID();
                storeData(tmpGUID, new InputStreamData(inputStream));

                File tmpCachedLocation = getAtomLocation(tmpGUID);
                guid = generateGUID(new URILocation(tmpCachedLocation.getPathname()));

                File cachedLocation = getAtomLocation(guid);

                // FIXME - use internal storage!!!!!
                FileHelper.renameFile(tmpCachedLocation.getPathname(), cachedLocation.getPathname());

                Location location = getLocation(guid);
                locationBundle = getBundle(location);

            } catch (GUIDGenerationException | SourceLocationException |
                    URISyntaxException | DataStorageException e) {
                throw new StorageException(e);
            }

        return guid;
        }

    @Override
    public LocationBundle getLocationBundle() {
        return locationBundle;
    }
}

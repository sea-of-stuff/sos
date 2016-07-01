package uk.ac.standrews.cs.sos.model.datastore;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.storage.data.InputStreamData;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;
import uk.ac.standrews.cs.sos.utils.FileHelper;

import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class StreamStore extends CommonStore {

    private InputStream inputStream;
    private LocationBundle locationBundle;

    public StreamStore(Configuration configuration, Storage storage, InputStream inputStream) {
        super(configuration, storage);
        this.inputStream = inputStream;
    }

    @Override
    public IGUID store() throws DataStorageException {
            IGUID guid;
            if (inputStream == null) {
                throw new DataStorageException();
            }

            try {
                IGUID tmpGUID = GUIDFactory.generateRandomGUID();
                storeData(tmpGUID, new InputStreamData((inputStream)));

                File tmpCachedLocation = getAtomLocation(tmpGUID);
                guid = generateGUID(new URILocation(tmpCachedLocation.getPathname()));

                File cachedLocation = getAtomLocation(guid);
                FileHelper.renameFile(tmpCachedLocation.getPathname(), cachedLocation.getPathname());

                Location location = getLocation(guid);
                locationBundle = getBundle(location);

            } catch (GUIDGenerationException | SourceLocationException | URISyntaxException e) {
                throw new DataStorageException();
            }

            return guid;
        }

    @Override
    public LocationBundle getLocationBundle() {
        return locationBundle;
    }
}
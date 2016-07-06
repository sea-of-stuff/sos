package uk.ac.standrews.cs.sos.model.datastore;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.utils.FileHelper;
import uk.ac.standrews.cs.storage.data.InputStreamData;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.File;
import uk.ac.standrews.cs.storage.interfaces.IStorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class StreamStore extends CommonStore {

    private InputStream inputStream;
    private LocationBundle locationBundle;

    public StreamStore(Configuration configuration, IStorage storage, InputStream inputStream) {
        super(configuration, storage);
        this.inputStream = inputStream;
    }

    @Override
    public IGUID store() throws StorageException {
            IGUID guid;
            if (inputStream == null) {
                throw new StorageException();
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

            } catch (GUIDGenerationException | SourceLocationException | URISyntaxException | IOException e) {
                throw new StorageException();
            }

        return guid;
        }

    @Override
    public LocationBundle getLocationBundle() {
        return locationBundle;
    }
}

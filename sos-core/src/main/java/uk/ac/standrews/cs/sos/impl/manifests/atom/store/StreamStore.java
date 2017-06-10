package uk.ac.standrews.cs.sos.impl.manifests.atom.store;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.castore.exceptions.RenameException;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class StreamStore extends CommonLocalStore {

    private InputStream inputStream;
    private LocationBundle locationBundle;

    public StreamStore(IGUID nodeGUID, LocalStorage storage, InputStream inputStream) {
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

                IFile tmpCachedLocation = getAtomLocation(tmpGUID);
                guid = generateGUID(new URILocation(tmpCachedLocation.getPathname()));

                tmpCachedLocation.rename(guid.toString());

                Location location = getLocation(guid);
                locationBundle = getBundle(location);

            } catch (GUIDGenerationException | SourceLocationException | URISyntaxException | DataStorageException | RenameException e) {
                throw new StorageException(e);
            }

        return guid;
        }

    @Override
    public LocationBundle getLocationBundle() {
        return locationBundle;
    }
}

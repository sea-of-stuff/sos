package uk.ac.standrews.cs.sos.impl.data;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.exceptions.RenameException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/*
 * This is the class that will take care of storing atom's data
 *
 * TODO - rename class to AtomStorage once the old class is deleted
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorage {

    private IGUID localNodeGUID;
    private LocalStorage localStorage;

    public DataStorage(IGUID localNodeGUID, LocalStorage storage) {
        this.localNodeGUID = localNodeGUID;
        this.localStorage = storage;
    }

    public Pair<IGUID, LocationBundle> persist(AtomBuilder atomBuilder) throws DataStorageException {

        try {
            IGUID guid = storeToLocalStorage(atomBuilder);
            Location localLocation = makeLocalSOSLocation(guid);
            LocationBundle bundle = new LocationBundle(BundleTypes.PERSISTENT, localLocation);

            return new Pair<>(guid, bundle);

        } catch (SourceLocationException e) {
            throw new DataStorageException("Unable to persist data properly");
        }

    }

    // Data is stored in disk, but marked as cached
    public Pair<IGUID, LocationBundle> cache(AtomBuilder atomBuilder) throws DataStorageException {

        try {
            IGUID guid = storeToLocalStorage(atomBuilder);
            Location localLocation = makeLocalSOSLocation(guid);
            LocationBundle bundle = new LocationBundle(BundleTypes.CACHE, localLocation);

            return new Pair<>(guid, bundle);
        } catch (SourceLocationException e) {
            throw new DataStorageException("Unable to persist data properly");
        }

    }

    private IGUID storeToLocalStorage(AtomBuilder atomBuilder) throws DataStorageException {

        if (!atomBuilder.isBuildIsSet()) {
            throw new DataStorageException("AtomBuilder not set correctly");
        }

        // Store data first and then assign valid GUID
        try {
            IGUID tmpGUID = GUIDFactory.generateRandomGUID();
            persistData(tmpGUID, atomBuilder);

            IFile tmpCachedLocation = atomFileInLocalStorage(tmpGUID);
            IGUID guid = generateGUID(new URILocation(tmpCachedLocation.getPathname()));
            tmpCachedLocation.rename(guid.toMultiHash());

            return guid;

        } catch (RenameException | URISyntaxException e) {
            throw new DataStorageException("Unable to persist data properly");
        }

    }

    private void persistData(IGUID guid, AtomBuilder atomBuilder) throws DataStorageException {

        if (atomBuilder.isData())
            persistData(guid, atomBuilder.getData());
        else if (atomBuilder.isLocation())
            persistData(guid, atomBuilder.getLocation());
        else
            throw new DataStorageException("AtomBuilder not set correctly");
    }

    private void persistData(IGUID guid, Location location) throws DataStorageException {

        try (InputStream dataStream = LocationUtility.getInputStreamFromLocation(location)) {

            InputStreamData data = new InputStreamData(dataStream);
            persistData(guid, data);
        } catch (IOException e) {
            throw new DataStorageException(e);
        }
    }

    private void persistData(IGUID guid, Data data) throws DataStorageException {

        try {
            IDirectory dataDirectory = localStorage.getDataDirectory();
            IFile file = localStorage.createFile(dataDirectory, guid.toMultiHash(), data);

            file.persist();
        } catch (PersistenceException e) {
            throw new DataStorageException(e);
        }
    }

    private IGUID generateGUID(Data data) throws GUIDGenerationException, IOException {

        return GUIDFactory.generateGUID(ALGORITHM.SHA256, data.getInputStream());
    }

    private IGUID generateGUID(Location location) {

        try (InputStream dataStream = LocationUtility.getInputStreamFromLocation(location)) {

            InputStreamData data = new InputStreamData(dataStream);
            return generateGUID(data);

        } catch (IOException | GUIDGenerationException e) {
            return new InvalidID();
        }
    }

    private IFile atomFileInLocalStorage(IGUID guid) throws DataStorageException {
        IDirectory dataDirectory = localStorage.getDataDirectory();
        return localStorage.createFile(dataDirectory, guid.toMultiHash());
    }

    private Location makeLocalSOSLocation(IGUID guid) throws SourceLocationException {

        try {
            return new SOSLocation(localNodeGUID, guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for entity: " + guid.toMultiHash(), e);
        }

    }

}

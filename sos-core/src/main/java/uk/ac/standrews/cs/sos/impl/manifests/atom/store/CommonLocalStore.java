package uk.ac.standrews.cs.sos.impl.manifests.atom.store;

import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonLocalStore implements Store {

    protected LocalStorage storage;
    protected IGUID nodeGUID;

    public CommonLocalStore(IGUID nodeGUID, LocalStorage storage) {
        this.nodeGUID = nodeGUID;
        this.storage = storage;
    }

    protected IGUID generateGUID(InputStream inputStream) throws GUIDGenerationException {
        IGUID retval;
        retval = GUIDFactory.generateGUID(inputStream);

        return retval;
    }

    protected IGUID generateGUID(Location location) throws GUIDGenerationException, SourceLocationException {
        IGUID retval = null;
        try (InputStream dataStream = LocationUtility.getInputStreamFromLocation(location)) {
            if (!(dataStream instanceof NullInputStream)) {
                retval = generateGUID(dataStream);
            }
        } catch (IOException e) {
            throw new SourceLocationException("IO Exception thrown while getting data for location " + location.toString(), e);
        }

        return retval;
    }

    protected Location getLocation(IGUID guid) throws SourceLocationException {

        try {
            return new SOSLocation(nodeGUID, guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for entity: " + guid.toString(), e);
        }

    }

    protected abstract LocationBundle getBundle(Location location);

    protected IFile getAtomLocation(IGUID guid) throws DataStorageException {
        IDirectory dataDirectory = storage.getDataDirectory();
        return storage.createFile(dataDirectory, guid.toString());
    }

    protected void storeData(IGUID guid, Data data) throws DataStorageException {
        IDirectory dataDirectory = storage.getDataDirectory();
        IFile file = storage.createFile(dataDirectory, guid.toString(), data);

        try {
            file.persist();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }
}

package uk.ac.standrews.cs.sos.model.datastore;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonStore implements Store {

    protected Configuration configuration;
    protected Storage storage;

    public CommonStore(Configuration configuration, Storage storage) {
        this.configuration = configuration;
        this.storage = storage;
    }

    protected IGUID generateGUID(InputStream inputStream) throws GUIDGenerationException {
        IGUID retval;
        retval = GUIDFactory.generateGUID(inputStream);

        return retval;
    }

    protected IGUID generateGUID(Location location) throws GUIDGenerationException, SourceLocationException {
        IGUID retval = null;
        try (InputStream dataStream =
                     StorageHelper.getInputStreamFromLocation(location)) {
            if (dataStream != null) {
                retval = generateGUID(dataStream);
            }
        } catch (IOException e) {
            throw new SourceLocationException(e);
        }

        return retval;
    }

    protected Location getLocation(IGUID guid) throws SourceLocationException {
        Location location;
        Node node = configuration.getNode();
        try {
            location = new SOSLocation(node.getNodeGUID(), guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for machine-guid: " +
                    node.toString() + " and entity: " + guid.toString());
        }
        return location;
    }

    protected abstract LocationBundle getBundle(Location location);

    protected File getAtomLocation(IGUID guid) {
        return storage.createFile(configuration.getDataDirectory(), guid.toString()); // FIXME - do not use config
    }

    protected void storeData(IGUID guid, Data data) {
        Directory dataDirectory = storage.getDataDirectory();
        File file = storage.createFile(dataDirectory, guid.toString(), data);

        try {
            file.persist();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }
}

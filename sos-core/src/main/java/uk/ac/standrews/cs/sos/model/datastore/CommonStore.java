package uk.ac.standrews.cs.sos.model.datastore;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;
import uk.ac.standrews.cs.sos.utils.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonStore implements Store {

    protected Configuration configuration;

    public CommonStore(Configuration configuration) {
        this.configuration = configuration;
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

    protected void storeData(InputStream inputStream, IGUID guid) throws DataStorageException {
        try {
            SOSFile cachedLocation = getAtomLocation(guid);
            String path = cachedLocation.getPathname();

            FileHelper.touchDir(path);
            if (!FileHelper.pathExists(path)) {
                FileHelper.copyToFile(inputStream, path);
            }
        } catch (IOException e) {
            throw new DataStorageException();
        }
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

    protected abstract SOSFile getAtomLocation(IGUID guid);

}

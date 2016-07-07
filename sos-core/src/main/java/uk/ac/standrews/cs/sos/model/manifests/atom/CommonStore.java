package uk.ac.standrews.cs.sos.model.manifests.atom;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.LocationUtility;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonStore implements Store {

    protected InternalStorage storage;

    public CommonStore(InternalStorage storage) {
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
                     LocationUtility.getInputStreamFromLocation(location)) {
            if (dataStream != null) {
                retval = generateGUID(dataStream);
            }
        } catch (IOException e) {
            throw new SourceLocationException(e);
        }

        return retval;
    }

    protected Location getLocation(IGUID guid) throws SourceLocationException {

        try {
            IGUID nodeGUID = SOSLocalNode.getInstance().getNodeGUID();
            return new SOSLocation(nodeGUID, guid);
        } catch (MalformedURLException | SOSException e) {
            throw new SourceLocationException("SOSLocation could not be generated for entity: " + guid.toString());
        }

    }

    protected abstract LocationBundle getBundle(Location location);

    protected File getAtomLocation(IGUID guid) throws IOException {
        return storage.createFile(storage.getDataDirectory(), guid.toString()); // FIXME - do not use config
    }

    protected void storeData(IGUID guid, Data data) throws IOException {
        Directory dataDirectory = storage.getDataDirectory();
        File file = storage.createFile(dataDirectory, guid.toString(), data);

        try {
            file.persist();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }
}

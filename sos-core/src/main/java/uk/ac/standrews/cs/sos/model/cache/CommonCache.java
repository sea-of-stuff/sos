package uk.ac.standrews.cs.sos.model.cache;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.storage.DataStorageHelper;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;
import uk.ac.standrews.cs.sos.utils.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonCache {

    protected SeaConfiguration configuration;

    public CommonCache(SeaConfiguration configuration) {
        this.configuration = configuration;
    }

    protected IGUID generateGUID(InputStream inputStream) throws GUIDGenerationException {
        IGUID retval;
        retval = GUIDFactory.generateGUID(inputStream);

        return retval;
    }

    protected IGUID generateGUID(Location location) throws SourceLocationException, GUIDGenerationException {
        IGUID retval = null;
        InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);

        if (dataStream != null) {
            retval = generateGUID(dataStream);
        }

        return retval;
    }

    protected void storeData(InputStream inputStream, IGUID guid) throws DataStorageException {
        try {
            SOSFile cachedLocation = getAtomCachedLocation(guid);
            String path = cachedLocation.getPathname();

            FileHelper.touchDir(path);
            if (!FileHelper.pathExists(path)) {
                FileHelper.copyToFile(inputStream, path);
            }
        } catch (IOException e) {
            throw new DataStorageException();
        }
    }

    protected CacheLocationBundle getCacheBundle(IGUID guid) throws SourceLocationException {
        Location location;
        Node node = configuration.getNode();
        try {
            location = new SOSLocation(node.getNodeGUID(), guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for machine-guid: " +
                    node.toString() + " and entity: " + guid.toString() );
        }
        return new CacheLocationBundle(location);
    }

    protected SOSFile getAtomCachedLocation(IGUID guid) {
        return new FileBasedFile(configuration.getCacheDirectory(), guid.toString());
    }
}

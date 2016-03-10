package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.FileHelper;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * TODO - javadoc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheDataStorage {

    private SeaConfiguration configuration;
    private LocationBundle origin;
    private CacheLocationBundle cache;

    public CacheDataStorage(SeaConfiguration configuration,
                            LocationBundle origin) {
        this.configuration = configuration;
        this.origin = origin;
    }

    /**
     * Store the data at the Location Bundles in the cache
     *
     * @return GUID generated from the data at the location bundles
     * @throws DataStorageException
     */
    public GUID cacheAtom()
            throws DataStorageException {

        GUID guid = null;
        if (origin == null) {
            throw new DataStorageException();
        }

        try {
            guid = generateGUID(origin);
            if (guid == null)
                return guid;

            storeData(configuration, origin, guid);
            try {
                cache = getCacheBundle(configuration, guid);
            } catch (SourceLocationException e) {
                throw new DataStorageException();
            }
        } catch (GuidGenerationException | SourceLocationException e) {
            e.printStackTrace();
        }

        return guid;
    }

    public CacheLocationBundle getCacheLocationBundle() {
        return this.cache;
    }



    private GUID generateGUID(LocationBundle bundle) throws SourceLocationException, GuidGenerationException {
        GUID retval = null;
        Location location = bundle.getLocation();
        InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);

        if (dataStream != null) {
            try {
                retval = new GUIDsha1(dataStream);
            } catch (GuidGenerationException e) {
                throw new GuidGenerationException();
            }
        }

        return retval;
    }

    private void storeData(SeaConfiguration configuration, LocationBundle bundle, GUID guid) throws DataStorageException {
        try {
            Location location = bundle.getLocation();
            InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);
            String cachedLocationPath = getAtomCachedLocation(configuration, guid);

            touchDir(cachedLocationPath);
            if (!pathExists(cachedLocationPath)) {
                FileHelper.copyToFile(dataStream, cachedLocationPath);
            }
        } catch (IOException | URISyntaxException | SourceLocationException e) {
            throw new DataStorageException();
        }
    }

    private CacheLocationBundle getCacheBundle(SeaConfiguration configuration, GUID guid) throws SourceLocationException {
        Location location;
        try {
            location = new SOSLocation(configuration.getNodeId(), guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for machine-guid: " +
                    configuration.getNodeId().toString() + " and entity: " + guid.toString() );
        }
        return new CacheLocationBundle(location);
    }

    private String getAtomCachedLocation(SeaConfiguration configuration, GUID guid) throws URISyntaxException {
        return configuration.getCacheDataPath() + guid.toString();
    }

    private void touchDir(String path) throws IOException {
        File parent = new File(path).getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            parent.mkdirs();
        }
    }

    private boolean pathExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}

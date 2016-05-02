package uk.ac.standrews.cs.sos.model.cache;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.storage.DataStorageHelper;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;
import uk.ac.standrews.cs.utils.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Cache an atom - given its location or data stream - to this SOS node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheManager {

    private final SeaConfiguration configuration;
    private Location origin;
    private InputStream inputStream;
    private CacheLocationBundle cache;

    // TODO - abstract cache types
    private static final int CACHE_ORIGIN_LOCATION_TYPE = 0;
    private static final int CACHE_ORIGIN_STREAM_TYPE = 1;

    private final int cacheOriginType;

    public CacheManager(SeaConfiguration configuration,
                        Location origin) {
        this.configuration = configuration;
        this.origin = origin;

        cacheOriginType = CACHE_ORIGIN_LOCATION_TYPE;
    }

    public CacheManager(SeaConfiguration configuration,
                        InputStream inputStream) {
        this.configuration = configuration;
        this.inputStream = inputStream;

        cacheOriginType = CACHE_ORIGIN_STREAM_TYPE;
    }

    /**
     * Store the data at the Location Bundles in the cache
     *
     * @return GUID generated from the data at the location bundles
     * @throws DataStorageException
     */
    public IGUID cacheAtom() throws DataStorageException {
        IGUID guid;

        switch(cacheOriginType) {
            case CACHE_ORIGIN_LOCATION_TYPE:
                guid = cacheAtomFromLocation();
                break;
            case CACHE_ORIGIN_STREAM_TYPE:
                guid = cacheAtomFromInputStream();
                break;
            default:
                throw new DataStorageException();
        }
        return guid;
    }

    private IGUID cacheAtomFromLocation() throws DataStorageException {
        IGUID guid;
        if (origin == null) {
            throw new DataStorageException();
        }

        try {
            guid = generateGUID(origin);
            if (guid == null) {
                return null;
            }

            storeData(configuration, origin, guid);
            cache = getCacheBundle(configuration, guid);
        } catch (GUIDGenerationException | SourceLocationException e) {
            throw new DataStorageException();
        }

        return guid;
    }

    private IGUID cacheAtomFromInputStream() throws DataStorageException {
        IGUID guid;
        if (inputStream == null) {
            throw new DataStorageException();
        }

        try {
            IGUID tmpGUID = GUIDFactory.generateRandomGUID();
            storeData(configuration, inputStream, tmpGUID);

            SOSFile tmpCachedLocation = getAtomCachedLocation(configuration, tmpGUID);
            guid = generateGUID(new URILocation(tmpCachedLocation.getPathname()));

            SOSFile cachedLocation = getAtomCachedLocation(configuration, guid);
            FileHelper.renameFile(tmpCachedLocation.getPathname(), cachedLocation.getPathname());
            cache = getCacheBundle(configuration, guid);

        } catch (GUIDGenerationException | SourceLocationException | URISyntaxException e) {
            throw new DataStorageException();
        }

        return guid;
    }

    public CacheLocationBundle getCacheLocationBundle() {
        return this.cache;
    }

    private IGUID generateGUID(Location location) throws SourceLocationException, GUIDGenerationException {
        IGUID retval = null;
        InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);

        if (dataStream != null) {
            retval = generateGUID(dataStream);
        }

        return retval;
    }

    private IGUID generateGUID(InputStream inputStream) throws GUIDGenerationException {
        IGUID retval;
        retval = GUIDFactory.generateGUID(inputStream);


        return retval;
    }

    private void storeData(SeaConfiguration configuration, Location location, IGUID guid) throws DataStorageException {
        try {
            InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);
            storeData(configuration, dataStream, guid);
        } catch (SourceLocationException e) {
            throw new DataStorageException();
        }
    }

    private void storeData(SeaConfiguration configuration, InputStream inputStream, IGUID guid) throws DataStorageException {
         try {
             SOSFile cachedLocation = getAtomCachedLocation(configuration, guid);
             String path = cachedLocation.getPathname();

             FileHelper.touchDir(path);
            if (!FileHelper.pathExists(path)) {
                FileHelper.copyToFile(inputStream, path);
            }
         } catch (IOException e) {
             throw new DataStorageException();
         }
    }

    private CacheLocationBundle getCacheBundle(SeaConfiguration configuration, IGUID guid) throws SourceLocationException {
        Location location;
        try {
            location = new SOSLocation(configuration.getNodeId(), guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for machine-guid: " +
                    configuration.getNodeId().toString() + " and entity: " + guid.toString() );
        }
        return new CacheLocationBundle(location);
    }

    private SOSFile getAtomCachedLocation(SeaConfiguration configuration, IGUID guid) {
        return new FileBasedFile(configuration.getCacheDirectory(), guid.toString());
    }

}

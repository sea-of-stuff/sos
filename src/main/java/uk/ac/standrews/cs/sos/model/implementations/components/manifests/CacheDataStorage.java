package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.FileHelper;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;

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
    private InputStream inputStream;
    private CacheLocationBundle cache;

    private static final int CACHE_ORIGIN_LOCATION_TYPE = 0;
    private static final int CACHE_ORIGIN_STREAM_TYPE = 1;
    private int cacheOriginType;

    public CacheDataStorage(SeaConfiguration configuration,
                            LocationBundle origin) {
        this.configuration = configuration;
        this.origin = origin;

        cacheOriginType = CACHE_ORIGIN_LOCATION_TYPE;
    }

    public CacheDataStorage(SeaConfiguration configuration,
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
    public GUID cacheAtom() throws DataStorageException {
        GUID guid = null;

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

    private GUID cacheAtomFromLocation() throws DataStorageException {
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

    private GUID cacheAtomFromInputStream() throws DataStorageException {
        GUID guid = null;
        if (inputStream == null) {
            throw new DataStorageException();
        }

        try {
            // cache and then get guid?
            GUID tmpGUID = GUID.generateRandomGUID();
            storeData(configuration, inputStream, tmpGUID);

            String cachedLocationPath = getAtomCachedLocation(configuration, guid);

            guid = generateGUID(new URILocation(cachedLocationPath));

            /*
            File oldfile =new File("oldfile.txt");
            File newfile =new File("newfile.txt");

            if(oldfile.renameTo(newfile)){
                System.out.println("Rename succesful");
            }else{
                System.out.println("Rename failed");
            }
             */
            // rename file with tmp GUID to guid

        } catch (GuidGenerationException | SourceLocationException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        return guid;
    }

    public CacheLocationBundle getCacheLocationBundle() {
        return this.cache;
    }

    private GUID generateGUID(LocationBundle bundle) throws SourceLocationException, GuidGenerationException {
        Location location = bundle.getLocation();
        return generateGUID(location);
    }

    private GUID generateGUID(Location location) throws SourceLocationException, GuidGenerationException {
        GUID retval = null;
        InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);

        if (dataStream != null) {
            retval = generateGUID(dataStream);
        }

        return retval;
    }

    private GUID generateGUID(InputStream inputStream) throws SourceLocationException, GuidGenerationException {
        GUID retval = null;
        try {
            retval = GUID.generateGUID(inputStream);
        } catch (GuidGenerationException e) {
            throw new GuidGenerationException();
        }

        return retval;
    }

    private void storeData(SeaConfiguration configuration, LocationBundle bundle, GUID guid) throws DataStorageException {
        try {
            Location location = bundle.getLocation();
            InputStream dataStream = DataStorageHelper.getInputStreamFromLocation(location);

            storeData(configuration, dataStream, guid);
        } catch (SourceLocationException e) {
            throw new DataStorageException();
        }
    }

    private void storeData(SeaConfiguration configuration, InputStream inputStream, GUID guid) throws DataStorageException {
         try {
            String cachedLocationPath = getAtomCachedLocation(configuration, guid);

            touchDir(cachedLocationPath);
            if (!pathExists(cachedLocationPath)) {
                FileHelper.copyToFile(inputStream, cachedLocationPath);
            }
         } catch (IOException | URISyntaxException e) {
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

    // TODO - move to helper
    private void touchDir(String path) throws IOException {
        File parent = new File(path).getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            parent.mkdirs();
        }
    }

    // TODO - move to helper
    private boolean pathExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}

package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.implementations.utils.FileHelper;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * TODO - javadoc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorage {

    /**
     * Store the data at the Location Bundles in the cache
     *
     * @param configuration
     * @param bundles
     * @return GUID generated from the data at the location bundles
     * @throws DataStorageException
     */
    // TODO - consider passing only one bundle/location!?
    // TODO - deal with locations that fail
    // TODO - do not: Assume that all other locations point to the same source.
    public static GUID storeAtom(SeaConfiguration configuration, Collection<LocationBundle> bundles) throws DataStorageException {
        GUID guid = null;
        if (bundles == null || bundles.isEmpty()) {
            throw new DataStorageException();
        }

        for(LocationBundle bundle:bundles) {
            try {
                guid = generateGUID(bundle);
                if (guid == null)
                    continue;

                storeData(configuration, bundle, guid);
                LocationBundle cachedBundle;
                try {
                    cachedBundle = getCacheBundle(configuration, guid);
                } catch (SourceLocationException e) {
                    throw new DataStorageException();
                }

                if (!bundles.contains(cachedBundle))
                    bundles.add(cachedBundle);

                break;
            } catch (GuidGenerationException e) {
                e.printStackTrace();
            } catch (SourceLocationException e) {
                e.printStackTrace();
            }
        }

        return guid;
    }

    public static InputStream getInputStreamFromLocation(Location location) throws SourceLocationException {
        InputStream stream;
        try {
            stream = location.getSource();
        } catch (IOException e) {
            throw new SourceLocationException("DataStorage " + location.toString() + " " + e);
        }

        return stream;
    }

    private static GUID generateGUID(LocationBundle bundle) throws SourceLocationException, GuidGenerationException {
        GUID retval = null;
        Location location = bundle.getLocation();
        InputStream dataStream = getInputStreamFromLocation(location);

        if (dataStream != null) {
            try {
                retval = new GUIDsha1(dataStream);
            } catch (GuidGenerationException e) {
                throw new GuidGenerationException();
            }
        }

        return retval;
    }

    private static void storeData(SeaConfiguration configuration, LocationBundle bundle, GUID guid) throws DataStorageException {
        try {
            Location location = bundle.getLocation();
            InputStream dataStream = getInputStreamFromLocation(location);
            String cachedLocationPath = getAtomCachedLocation(configuration, guid);

            touchDir(cachedLocationPath);
            if (!pathExists(cachedLocationPath)) {
                FileHelper.copyToFile(dataStream, cachedLocationPath);
            }
        } catch (IOException | URISyntaxException | SourceLocationException e) {
            throw new DataStorageException();
        }
    }

    private static LocationBundle getCacheBundle(SeaConfiguration configuration, GUID guid) throws SourceLocationException {
        Location location;
        try {
            location = new SOSLocation(configuration.getMachineID(), guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for machine-guid: " +
                    configuration.getMachineID().toString() + " and entity: " + guid.toString() );
        }
        return new CacheLocationBundle(location);
    }

    private static String getAtomCachedLocation(SeaConfiguration configuration, GUID guid) throws URISyntaxException {
        return configuration.getCacheDataPath() + guid.toString();
    }

    private static void touchDir(String path) throws IOException {
        File parent = new File(path).getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            parent.mkdirs();
        }
    }

    private static boolean pathExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}

package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.OldLocation;
import uk.ac.standrews.cs.sos.model.implementations.utils.FileHelper;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorage {

    public static InputStream getInputStreamFromLocation(Location location) throws SourceLocationException {
        InputStream stream;
        try {
            stream = location.getSource();
        } catch (IOException e) {
            throw new SourceLocationException(location.getURI().toString());
        }

        return stream;
    }

    public static InputStream getInputStreamFromLocations(Location[] locations) throws SourceLocationException {
        InputStream stream;
        try {
            stream = locations[0].getSource();
            for(int i = 1; i < locations.length; i++) {
                stream = new SequenceInputStream(stream, locations[i].getSource());
            }
        } catch (IOException e) {
            throw new SourceLocationException(locations.toString()); // TODO - better output for printing array of location
        }

        return stream;
    }

    // TODO - consider passing only one bundle/location!?
    public static GUID storeAtom(SeaConfiguration configuration, Collection<LocationBundle> bundles) throws DataStorageException {

        // TODO - deal with locations that fail

        GUID guid = null;
        if (bundles == null || bundles.isEmpty()) {
            throw new DataStorageException(); // TODO - SourceLocationException
        }

        // TODO - prioritise bundles based on type
        for(LocationBundle bundle:bundles) {
            InputStream dataStream;
            try {
                dataStream = getInputStreamFromLocation(bundle.getLocations()[0]); // TODO - assume only one location
            } catch (SourceLocationException e) {
                continue;
            }

            if (dataStream != null) {
                try {
                    guid = new GUIDsha1(dataStream);
                } catch (GuidGenerationException e) {
                    throw new DataStorageException(); // TODO - use different exception?
                }

                boolean successful = storeData(configuration, bundle, guid);
                if (successful) {
                    LocationBundle cachedBundle = cacheBundle(bundle);
                    bundles.add(cachedBundle);
                }

                break; // FIXME - do not: Assume that all other locations point to the same source.
            }
        }

        // TODO - Annotate locations

        return guid;
    }

    private static boolean storeData(SeaConfiguration configuration, LocationBundle bundle, GUID guid) throws DataStorageException {
        boolean retval = false;
        try {
            Location location = bundle.getLocations()[0];  // FIXME - assume only one location
            InputStream dataStream = getInputStreamFromLocation(location);
            String cachedLocationPath = getAtomCachedLocation(configuration, guid);

            touchDir(cachedLocationPath);
            if (!pathExists(cachedLocationPath)) {
                FileHelper.copyToFile(dataStream, cachedLocationPath);
                retval = true;
            }
        } catch (IOException | URISyntaxException | SourceLocationException e) {
            throw new DataStorageException();
        }
        return retval;
    }

    private static LocationBundle cacheBundle(LocationBundle bundle) {
        // TODO - make this bundle of type cache
        return null;
    }

    private static String getAtomCachedLocation(SeaConfiguration configuration, GUID guid) throws URISyntaxException {
        return configuration.getCacheDataPath() + guid.toString();
    }

    private static void removeUserLocations(SeaConfiguration configuration, Collection<OldLocation> locations) {
        Iterator<OldLocation> it = locations.iterator();
        while(it.hasNext()) {
            OldLocation location = it.next();
            if (locationIsLocal(location) && !locationStartsWith(location, configuration.getCacheDataPath())) {
                it.remove();
            }
        }
    }

    private static boolean locationIsLocal(OldLocation location) {
        return location.getProtocol() != null && location.getProtocol().equals("file");
    }

    private static boolean locationStartsWith(OldLocation location, String dir) {
        String path = location.getLocationPath().getPath();
        return path.startsWith(dir);
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

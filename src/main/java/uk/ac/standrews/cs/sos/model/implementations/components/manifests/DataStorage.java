package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.implementations.utils.FileHelper;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * TODO - remove assumptions about bundles containing only one location!
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorage {

    public static InputStream getInputStreamFromLocation(Location location) throws SourceLocationException, IOException, URISyntaxException {
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
                dataStream = getInputStreamFromLocation(bundle.getLocations()[0]); // FIXME - assume only one location
            } catch (SourceLocationException | URISyntaxException | IOException e) {
                continue;
            }

            if (dataStream != null) {
                try {
                    guid = new GUIDsha1(dataStream);
                } catch (GuidGenerationException e) {
                    throw new DataStorageException(); // TODO - use different exception?
                }

                storeData(configuration, bundle, guid);
                LocationBundle cachedBundle = getCacheBundle(configuration, guid);
                if (!bundles.contains(cachedBundle))
                    bundles.add(cachedBundle);

                break; // FIXME - do not: Assume that all other locations point to the same source.
            }
        }

        // TODO - Annotate locations

        return guid;
    }

    private static void storeData(SeaConfiguration configuration, LocationBundle bundle, GUID guid) throws DataStorageException {
        try {
            Location location = bundle.getLocations()[0];  // FIXME - assume only one location
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

    private static LocationBundle getCacheBundle(SeaConfiguration configuration, GUID guid) {
        Location location = new SOSLocation(configuration.getMachineID(), guid);
        return new LocationBundle("cache", new Location[]{location});
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

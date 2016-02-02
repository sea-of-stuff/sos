package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.utils.FileHelper;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
            throw new SourceLocationException(location.getLocationPath().toString());
        }

        return stream;
    }

    public static Collection<Location> storeAtom(SeaConfiguration configuration, Collection<Location> locations) throws DataStorageException {
        if (locations == null || locations.isEmpty()) {
            throw new DataStorageException();
        }

        Collection<Location> newLocations = new ArrayList<>(locations);
        for(Location location:newLocations) {
            InputStream dataStream;
            try {
                dataStream = getInputStreamFromLocation(location);
            } catch (SourceLocationException e) {
                continue;
            }

            if (dataStream != null) {
                try {
                    Location newLocation = getAtomLocalLocation(configuration, location);
                    if (!location.equals(newLocation)) {
                        newLocations.add(newLocation);
                        touchDir(newLocation);
                    }
                    if (!dataInLocationAlreadyExists(newLocation)) {
                        FileHelper.copyToFile(dataStream, newLocation);
                    }
                } catch (IOException | URISyntaxException e) {
                    throw new DataStorageException();
                }
                break; // Assume that all other locations point to the same source.
            }
        }

        removeUserLocations(configuration, newLocations);
        return newLocations;
    }

    private static Location getAtomLocalLocation(SeaConfiguration configuration, Location location) throws URISyntaxException {
        URI uri = location.getLocationPath();
        String filename = getNameFromURI(uri);
        return new Location("file://" + configuration.getCacheDataPath() + filename);
    }

    private static String getNameFromURI(URI uri) {
        String[] segments = uri.getPath().split("/");
        return segments[segments.length-1];
    }

    private static void removeUserLocations(SeaConfiguration configuration, Collection<Location> locations) {
        Iterator<Location> it = locations.iterator();
        while(it.hasNext()) {
            Location location = it.next();
            if (locationIsLocal(location) && !locationStartsWith(location, configuration.getCacheDataPath())) {
                it.remove();
            }
        }
    }

    private static boolean locationIsLocal(Location location) {
        return location.getProtocol() != null && location.getProtocol().equals("file");
    }

    private static boolean locationStartsWith(Location location, String dir) {
        String path = location.getLocationPath().getPath();
        return path.startsWith(dir);
    }

    private static void touchDir(Location location) throws IOException {
        File parent = new File(location.getLocationPath()).getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            parent.mkdirs();
        }
    }

    private static boolean dataInLocationAlreadyExists(Location location) {
        File file = new File(location.getLocationPath());
        return file.exists();
    }
}

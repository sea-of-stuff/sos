package uk.ac.standrews.cs.sos.model.implementations.utils;

import uk.ac.standrews.cs.sos.model.implementations.locations.OldLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileHelper {

    public static void deleteFile(final String path) {
        File file = new File(path);
        file.delete();
    }

    public static void copyToFile(InputStream inputStream, OldLocation destination) throws IOException {
        Files.copy(inputStream, locationToPath(destination));
    }

    public static Path locationToPath(OldLocation location) {
        return new File(location.getLocationPath().getPath()).toPath();
    }
}

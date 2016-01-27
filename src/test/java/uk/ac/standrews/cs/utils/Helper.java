package uk.ac.standrews.cs.utils;

import uk.ac.standrews.cs.sos.model.implementations.utils.Location;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    public static String localURItoPath(Location location) throws URISyntaxException {
        return location.getLocationPath().getPath();
    }

    public static void deleteFile(String file) {
        Path path = Paths.get(file);
        deleteFile(path);
    }

    private static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s: not empty", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }

    }
}

package utils;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    public static void deleteFile(String file) {
        Path path = Paths.get(file);
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

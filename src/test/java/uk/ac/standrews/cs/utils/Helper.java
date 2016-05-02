package uk.ac.standrews.cs.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;

import java.io.*;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    private static String localURItoPath(Location location) throws URISyntaxException {
        return location.getURI().getPath();
    }

    public static Location createDummyDataFile(SeaConfiguration configuration) throws FileNotFoundException, URISyntaxException {
        return createDummyDataFile(configuration, "testData.txt");
    }

    public static Location createDummyDataFile(SeaConfiguration configuration, String filename) throws FileNotFoundException, URISyntaxException {
        return createDummyDataFile(configuration.getDataPath(), filename);
    }

    public static Location createDummyDataFile(SOSDirectory sosParent, String filename) throws FileNotFoundException, URISyntaxException {
        SOSFile sosFile = new FileBasedFile(sosParent, filename);

        File file = sosFile.toFile();
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("The first line");
            writer.println("The second line");
        }

        return new URILocation("file://"+sosFile.getPathname());
    }

    public static void appendToFile(Location location, String text) throws URISyntaxException, IOException {

        try (PrintWriter writer = new PrintWriter(
                new FileOutputStream(
                new File(Helper.localURItoPath(location)), true))) {
            writer.append(text);
        }
    }

    public static void cleanDirectory(SOSDirectory directory) throws IOException {
        File dir = new File(directory.getPathname());

        if (dir.exists()) {
            FileUtils.cleanDirectory(dir);
        }
    }

    public static void deleteDirectory(SOSDirectory directory) throws IOException {
        File dir = new File(directory.getPathname());

        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
    }

    public static String InputStreamToString(InputStream stream) throws IOException {
        return IOUtils.toString(stream);
    }
}

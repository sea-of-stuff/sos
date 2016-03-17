package uk.ac.standrews.cs.utils;

import org.apache.commons.io.FileUtils;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;

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

    public static Location createDummyDataFile(String path, String filename) throws FileNotFoundException, URISyntaxException {
        String location = path + filename;

        File file = new File(location);
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        PrintWriter writer = new PrintWriter(file);
        writer.println("The first line");
        writer.println("The second line");
        writer.close();

        return new URILocation("file://"+location);
    }

    public static void appendToFile(Location location, String text) throws URISyntaxException, IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(
                new File(Helper.localURItoPath(location)),
                true));

        writer.append(text);
        writer.close();
    }

    public static void cleanDirectory(String path) throws IOException {
        File dir = new File(path);

        if (dir.exists()) {
            FileUtils.cleanDirectory(dir);
        }
    }

    public static void deleteDirectory(String path) throws IOException {
        File dir = new File(path);

        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
    }
}

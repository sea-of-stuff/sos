package uk.ac.standrews.cs.utils;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.ProvenanceLocationBundle;

import java.io.*;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    private static String localURItoPath(Location location) throws URISyntaxException, IOException {
        return location.getURI().getPath();
    }

    public static LocationBundle createDummyDataFile(SeaConfiguration configuration) throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
        return createDummyDataFile(configuration, "testData.txt");
    }

    public static LocationBundle createDummyDataFile(SeaConfiguration configuration, String filename) throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
        return createDummyDataFile(configuration.getDataPath(), filename);
    }

    public static LocationBundle createDummyDataFile(String path, String filename) throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
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

        return new ProvenanceLocationBundle(new URILocation("file://"+location));
    }

    public static void appendToFile(Location location, String text) throws URISyntaxException, IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(
                new File(Helper.localURItoPath(location)),
                true));

        writer.append(text);
        writer.close();
    }

}

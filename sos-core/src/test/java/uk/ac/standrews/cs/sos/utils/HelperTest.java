package uk.ac.standrews.cs.sos.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.implementations.filesystem.FileBasedFile;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HelperTest {

    public static InputStream StringToInputStream(String input) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String InputStreamToString(InputStream string) throws IOException {
        return IOUtils.toString(string, StandardCharsets.UTF_8);
    }

    private static String localURItoPath(Location location) throws URISyntaxException {
        return location.getURI().getPath();
    }

    public static Location createDummyDataFile(LocalStorage storage) throws URISyntaxException, StorageException, DataStorageException {
        return createDummyDataFile(storage, "testData.txt");
    }

    public static Location createDummyDataFile(LocalStorage storage, String filename)
            throws URISyntaxException, StorageException, DataStorageException {
        Directory testDir = storage.getDataDirectory();
        return createDummyDataFile(testDir, filename);
    }

    private static Location createDummyDataFile(Directory sosParent, String filename) throws URISyntaxException, StorageException {

        Data data = new StringData("The first line\nThe second line");
        File sosFile = new FileBasedFile(sosParent, filename, data, false);
        sosFile.persist();

        return new URILocation("file://" + sosFile.getPathname());
    }

    public static void appendToFile(Location location, String text) throws URISyntaxException, IOException {

        try (PrintWriter writer = new PrintWriter(
                new FileOutputStream(
                        new java.io.File(HelperTest.localURItoPath(location)), true))) {
            writer.append(text);
        }
    }

    public static void DeletePath(Directory directory) throws IOException {
        java.io.File dir = new java.io.File(directory.getPathname());

        if (dir.exists()) {
            FileUtils.cleanDirectory(dir);
        }
    }

    public static void DeletePath(String path) throws IOException {
        java.io.File dir = new java.io.File(path);

        if (dir.isFile() && dir.getParentFile().exists()) {
            FileUtils.cleanDirectory(dir.getParentFile());
        } else if (dir.isDirectory() && dir.exists()) {
            FileUtils.cleanDirectory(dir);
        }
    }

}

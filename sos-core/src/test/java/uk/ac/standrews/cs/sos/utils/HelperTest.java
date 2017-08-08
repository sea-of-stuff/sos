package uk.ac.standrews.cs.sos.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.implementations.filesystem.FileBasedFile;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HelperTest {

    public static InputStream StringToInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String InputStreamToString(InputStream string) throws IOException {
        return IOUtils.toString(string, StandardCharsets.UTF_8);
    }

    public static String InputStreamToString64(InputStream stream) throws IOException {
        byte[] encodedBytes = Base64.getEncoder().encode(IOUtils.toByteArray(stream));
        return new String(encodedBytes);
    }

    private static String localSOSDataPath(LocalStorage localStorage, SOSLocation location) throws DataStorageException {

        return localStorage.getDataDirectory().toString() + location.getEntityID().toMultiHash();
    }

    public static Location createDummyDataFile(LocalStorage storage) throws URISyntaxException, StorageException, DataStorageException {
        return createDummyDataFile(storage, "testData.txt");
    }

    public static Location createDummyDataFile(LocalStorage storage, String filename)
            throws URISyntaxException, StorageException, DataStorageException {
        IDirectory testDir = storage.getDataDirectory();
        return createDummyDataFile(testDir, filename);
    }

    private static Location createDummyDataFile(IDirectory sosParent, String filename) throws URISyntaxException, StorageException {

        Data data = new StringData("The first line\nThe second line");
        IFile sosFile = new FileBasedFile(sosParent, filename, data);
        sosFile.persist();

        return new URILocation("file://" + sosFile.getPathname());
    }

    public static void appendToFile(LocalStorage localStorage, SOSLocation location, String text) throws URISyntaxException, IOException, DataStorageException {

        String path = HelperTest.localSOSDataPath(localStorage, location);
        java.io.File file = new java.io.File(path);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);
             PrintWriter writer = new PrintWriter(fileOutputStream)) {

            writer.append(text);
        }
    }

    public static void DeletePath(IDirectory directory) throws IOException {
        java.io.File dir = new java.io.File(directory.getPathname());

        if (dir.exists()) {
            FileUtils.cleanDirectory(dir);
        }
    }

    public static void DeletePath(String path) throws IOException {
        java.io.File dir = new java.io.File(path);

        if (dir.isFile() && dir.getParentFile() != null && dir.getParentFile().exists()) {
            FileUtils.cleanDirectory(dir.getParentFile());
        } else if (dir.isDirectory() && dir.exists()) {
            FileUtils.cleanDirectory(dir);
        } else if (dir.isFile()) {
            dir.delete();
        }
    }

}

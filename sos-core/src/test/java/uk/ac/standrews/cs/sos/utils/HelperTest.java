package uk.ac.standrews.cs.sos.utils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SQLDB;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.implementations.filesystem.FileBasedFile;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HelperTest {

    public static InputStream StringToInputStream(String input) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String localURItoPath(Location location) throws URISyntaxException {
        return location.getURI().getPath();
    }

    public static Location createDummyDataFile(InternalStorage storage) throws IOException, URISyntaxException, PersistenceException {
        return createDummyDataFile(storage, "testData.txt");
    }

    public static Location createDummyDataFile(InternalStorage storage, String filename)
            throws IOException, URISyntaxException, PersistenceException {
        Directory testDir = storage.getDataDirectory();
        return createDummyDataFile(testDir, filename);
    }

    private static Location createDummyDataFile(Directory sosParent, String filename) throws IOException, URISyntaxException, PersistenceException {

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

    public static String InputStreamToString(InputStream stream) throws IOException {
        return IOUtils.toString(stream);
    }

    public static void DeletePath(Directory directory) throws IOException {
        java.io.File dir = new java.io.File(directory.getPathname());

        if (dir.exists()) {
            FileUtils.cleanDirectory(dir);
        }
    }

    public static void CreateDBTestDump() throws DatabasePersistenceException, SQLException, PersistenceException, IOException {
        Config.db_type = Config.DB_TYPE_SQLITE;
        Config.initDatabaseInfo();

        ConnectionSource connection = SQLDB.getSQLConnection();
        TableUtils.createTableIfNotExists(connection, Config.class);

        try {
            SQLDB.getConfiguration(connection);
        } catch (SQLException e) {
            Config config = new Config();
            Dao<Config, String> nodesDAO = DaoManager.createDao(connection, Config.class);
            nodesDAO.create(config);
        }
    }

}

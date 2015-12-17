package sos.managers;

import sos.model.implementations.utils.GUID;
import sos.model.interfaces.components.Manifest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SQLiteCache extends MemCache {

    private static SQLiteCache instance = null;
    private static Connection connection;

    // Suppresses default constructor, ensuring non-instantiability.
    private SQLiteCache() throws SQLException {
        // Create an in-memory sqlite db instance as described here:
        // https://github.com/xerial/sqlite-jdbc
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        initTables();
    }

    public static SQLiteCache getInstance() throws SQLException {
        if(instance == null) {
            instance = new SQLiteCache();
        }
        return instance;
    }

    public void killInstance() {
        try {
            connection.close();
        } catch (SQLException e) {
            // TODO
            e.printStackTrace();
        }
        instance = null;
    }

    @Override
    public void flushDB() {

    }

    @Override
    public void addManifest(Manifest manifest) {
        // strip manifest
        // add contents to manifest
    }

    @Override
    public String getManifestType(GUID manifestGUID) {
        return null;
    }

    @Override
    public Collection<String> getLocations(GUID manifestGUID) {
        return null;
    }

    @Override
    public String getContent(GUID manifestGUID) {
        return null;
    }

    @Override
    public String getSignature(GUID manifestGUID) {
        return null;
    }

    @Override
    public Set<String> getManifests(GUID guid) {
        return null;
    }

    @Override
    public Set<String> getContents(GUID contentGUID) {
        return null;
    }

    @Override
    public String getIncarnation(GUID manifestGUID) {
        return null;
    }

    @Override
    public Set<String> getPrevs(GUID manifestGUID) {
        return null;
    }

    @Override
    public String getMetadata(GUID manifestGUID) {
        return null;
    }

    @Override
    public Set<String> getMetaValueMatches(String value) {
        return null;
    }

    @Override
    public Set<String> getMetaTypeMatches(String type) {
        return null;
    }


    private HashMap<GUID, Object> stripManifest(Manifest manifest) {
        return null;
    }

    private void initTables() throws SQLException {

        // create table tbl1(one varchar(10), two smallint);
        // PreparedStatement createTable = connection.prepareStatement("CREATE TABLE manifests(GUID VARCHAR(40))");

    }
}

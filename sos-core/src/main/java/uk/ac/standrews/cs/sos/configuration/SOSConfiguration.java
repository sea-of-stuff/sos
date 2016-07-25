package uk.ac.standrews.cs.sos.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.node.database.DatabaseType;
import uk.ac.standrews.cs.storage.StorageType;

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSConfiguration {

    Configuration configuration;

    /**
     * Create a configuration using the specified file (must be accessibly locally)
     * @param file
     */
    public SOSConfiguration(File file) throws SOSConfigurationException {
        Configurations configs = new Configurations();

        try {
            configuration = configs.properties(file);
        } catch (ConfigurationException e) {
            throw new SOSConfigurationException(e);
        }

    }

    public IGUID getNodeGUID() throws GUIDGenerationException, IOException {
        String defaultGuid = GUIDFactory.generateRandomGUID().toString();
        String guidString = configuration.getString(PropertyKeys.NODE_GUID, defaultGuid);

        if (guidString.equals(defaultGuid)) {
            setProperty(PropertyKeys.NODE_GUID, defaultGuid);
        }

        return GUIDFactory.recreateGUID(guidString);
    }

    public String getNodeHostname() {
        return configuration.getString(PropertyKeys.NODE_HOSTNAME);
    }

    public int getNodePort() {
        return configuration.getInt(PropertyKeys.NODE_PORT);
    }

    public boolean nodeIsClient() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_CLIENT);
    }

    public boolean nodeIsStorage() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_STORAGE);
    }

    public boolean nodeIsCoordinator() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_COORDINATOR);
    }

    public DatabaseType getDBType() {
        return new DatabaseType(configuration.getString(PropertyKeys.DB_TYPE));
    }

    public String getDBPath() {
        String path = configuration.getString(PropertyKeys.DB_PATH);

        if (path.charAt(0) == '~') {
            path = System.getProperty("user.home") + path.substring(1);
        }

        return path;
    }

    public StorageType getStorageType() {
        return StorageType.getEnum(configuration.getString(PropertyKeys.STORAGE_TYPE));
    }

    public String getStorageLocation() {

        String base = "";

        StorageType type = getStorageType();
        if (type.equals(StorageType.LOCAL)) {
            base = System.getProperty("user.home");
        }

        return base + configuration.getString(PropertyKeys.STORAGE_LOCATION);
    }

    public String getKeyFolderPath() {
        String path = configuration.getString(PropertyKeys.KEYS_FOLDER);

        if (path.charAt(0) == '~') {
            path = System.getProperty("user.home") + path.substring(1);
        }

        return path;
    }

    private void setProperty(String key, String value) throws IOException {
        configuration.setProperty(key, value);
    }

    private class PropertyKeys {

        public static final String NODE_GUID = "node.guid";
        public static final String NODE_HOSTNAME = "node.hostname";
        public static final String NODE_PORT = "node.port";
        public static final String NODE_IS_CLIENT = "node.is.client";
        public static final String NODE_IS_STORAGE = "node.is.storage";
        public static final String NODE_IS_COORDINATOR = "node.is.coordinator";


        public static final String DB_TYPE = "db.type";
        public static final String DB_PATH = "db.path";
        public static final String DB_HOSTNAME = "db.hostname";
        public static final String DB_USERNAME = "db.username";
        public static final String DB_PASSWORD = "db.password";

        public static final String STORAGE_TYPE = "storage.type";
        public static final String STORAGE_HOSTNAME = "storage.hostname";
        public static final String STORAGE_LOCATION = "storage.location";
        public static final String STORAGE_USERNAME = "storage.username";
        public static final String STORAGE_PASSWORD = "storage.password";
        public static final String STORAGE_ACCESS_KEY = "storage.access.key";
        public static final String STORAGE_SECRET_KEY = "storage.secret.key";

        public static final String KEYS_FOLDER = "keys.folder";
    }
}

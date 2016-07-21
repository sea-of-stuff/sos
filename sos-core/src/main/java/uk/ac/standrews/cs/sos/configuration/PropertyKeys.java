package uk.ac.standrews.cs.sos.configuration;

/**
 * List of PropertyKeys. Use these to facilitate access to the configuration
 * properties in #Configuration
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PropertyKeys {

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

package uk.ac.standrews.cs.sos.node;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.implementations.filesystem.FileBasedDirectory;
import uk.ac.standrews.cs.sos.storage.implementations.filesystem.FileBasedFile;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;

/**
 * This class contains all information to configure this SOS node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@DatabaseTable(tableName = "configuration")
public class Config {

    // no-args constructor needed for ORMLite
    public Config() {}

    /*
     * DEFAULT CONFIG VALUES - START
     */
    private static final String HOME = System.getProperty("user.home") + "/";
    private static final Directory HOME_DIR = new FileBasedDirectory(new java.io.File(HOME));
    private static final Directory ROOT_DIRECTORY_DEFAULT = new FileBasedDirectory(HOME_DIR, "sos", false);
    private static final String INDEX_DIRECTORY_NAME = "index";
    private static final String KEYS_DIRECTORY_NAME = "keys";
    private static final String DATABASE_DIRECTORY_NAME_DEFAULT = "db";

    private static final String PRIVATE_KEY_FILE = "private.der";
    private static final String PUBLIC_KEY_FILE = "public.der";
    private static final String DB_DUMP_FILE_NAME_DEFAULT = "dump.db";
    /*
     * DEFAULT CONFIG VALUES - END
     */

    private static Directory INDEX_DIRECTORY;
    private static Directory KEYS_DIRECTORY;

    public static Directory DB_DIRECTORY;
    public static File DB_DUMP_FILE;

    private static Directory root = ROOT_DIRECTORY_DEFAULT;

    // Database
    public final static String DB_TYPE_SQLITE = "sqlite";
    public final static String DB_TYPE_MYSQL = "mysql";

    // Initialise with default values
    public static String db_type = DB_TYPE_SQLITE;
    public static String db_path = DATABASE_DIRECTORY_NAME_DEFAULT;
    public static String db_hostname;
    public static String db_username;
    public static String db_password;

    // Node (this)
    @DatabaseField(id = true)
    private String n_id;
    @DatabaseField(canBeNull = true)
    private String n_hostname;
    @DatabaseField(canBeNull = true)
    private int n_port;
    @DatabaseField(canBeNull = true)
    private int n_roles;

    // Storage
    public final static String S_TYPE_LOCAL = "local";
    public final static String S_TYPE_NETWORK = "network";
    public final static String S_TYPE_AWS_S3 = "aws_s3";

    @DatabaseField(canBeNull = true)
    public String s_type = S_TYPE_LOCAL;
    @DatabaseField(canBeNull = true)
    public String s_hostname; // Will be used if storage is over the network
    @DatabaseField(canBeNull = true)
    public String s_location = root.getPathname(); // This is the folder where we store internal properties of system (e.g. manifests, etc)
    public String s_username;
    public String s_password;
    public String s_access_key;
    public String s_secret_key;

    public static void initDatabaseInfo() throws PersistenceException {
        DB_DIRECTORY = new FileBasedDirectory(root, db_path, false); // FIXME - do not use FileBasedDirectory! (move this to SQLConnection?)
        if (!DB_DIRECTORY.exists()) {
            DB_DIRECTORY.persist();
        }
        DB_DUMP_FILE = new FileBasedFile(DB_DIRECTORY, DB_DUMP_FILE_NAME_DEFAULT, false);
    }

}
package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedDirectory;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;
import uk.ac.standrews.cs.sos.node.SOSNode;

import java.io.*;

/**
 * Singleton configuration class. This class contains any information about this
 * SOS node configuration.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Configuration {

    private static final String HOME = System.getProperty("user.home") + "/";
    private static final String SOS_ROOT = HOME + "sos/";
    private static final SOSDirectory ROOT_DIRECTORY = new FileBasedDirectory(HOME + "sos");
    private static final String DEFAULT_ROOT_NAME = "";

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String CACHED_DATA_DIRECTORY_NAME = "cached_data";
    private static final String INDEX_DIRECTORY_NAME = "index";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String KEYS_DIRECTORY_NAME = "keys";
    private static final String DATABASE_DIRECTORY_NAME = "db";

    private static final String NODE_FILE = "node.txt";
    private static final String NODE_ROLES_FILE = "roles.txt";
    private static final String PRIVATE_KEY_FILE = "private.der";
    private static final String PUBLIC_KEY_FILE = "public.der";
    private static final String DB_DUMP_FILE_NAME = "test.db";

    private static SOSDirectory DATA_DIRECTORY;
    private static SOSDirectory CACHE_DIRECTORY;
    private static SOSDirectory INDEX_DIRECTORY;
    private static SOSDirectory MANIFEST_DIRECTORY;
    private static SOSDirectory KEYS_DIRECTORY;
    private static SOSDirectory DB_DIRECTORY;
    private static SOSFile DB_DUMP_FILE;

    private static String rootName;
    private static Node node;

    private static Configuration instance;
    private Configuration() {}

    /**
     * Return a Configuration instance for this SOS node.
     * @return configuration
     * @throws ConfigurationException
     */
    public static Configuration getInstance() throws ConfigurationException {
        if(instance == null) {
            if (Configuration.rootName == null) {
                Configuration.rootName = DEFAULT_ROOT_NAME;
            }

            initDirectories();
            initDB();
            loadSOSNode();

            instance = new Configuration();
        }
        return instance;
    }

    public static void setRootName(String rootName) {
        if (Configuration.rootName == null) {
            Configuration.rootName = rootName;
        }
    }

    public Node getNode() {
     return node;
    }

    public void setNode(Node node) throws ConfigurationException {
        if (Configuration.node == null) {
            Configuration.node = node;
            saveConfiguration();
        }
    }

    public SOSDirectory getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public SOSDirectory getManifestsDirectory() {
        return MANIFEST_DIRECTORY;
    }

    public SOSFile[] getIdentityPaths() {
         return new SOSFile[] { new FileBasedFile(KEYS_DIRECTORY, PRIVATE_KEY_FILE),
                 new FileBasedFile(KEYS_DIRECTORY, PUBLIC_KEY_FILE) };
    }

    public SOSDirectory getIndexDirectory() {
         return INDEX_DIRECTORY;
    }

    public SOSDirectory getCacheDirectory() {
        return CACHE_DIRECTORY;
    }

    public SOSFile getDatabaseDump() {
        return DB_DUMP_FILE;
    }

    public void saveConfiguration() throws ConfigurationException {
        try (BufferedWriter writer = new BufferedWriter
                (new FileWriter(SOS_ROOT + NODE_FILE)) ){
            if (node != null) {
                writer.write(node.toString());
            }
        } catch (IOException e) {
            throw new ConfigurationException();
        }
    }

    private static void initDirectories() {
        SOSDirectory root = new FileBasedDirectory(ROOT_DIRECTORY, Configuration.rootName);
        DATA_DIRECTORY = new FileBasedDirectory(root, DATA_DIRECTORY_NAME);
        CACHE_DIRECTORY = new FileBasedDirectory(root, CACHED_DATA_DIRECTORY_NAME);
        INDEX_DIRECTORY = new FileBasedDirectory(root, INDEX_DIRECTORY_NAME);
        MANIFEST_DIRECTORY = new FileBasedDirectory(root, MANIFESTS_DIRECTORY_NAME);
        KEYS_DIRECTORY = new FileBasedDirectory(root, KEYS_DIRECTORY_NAME);
        DB_DIRECTORY = new FileBasedDirectory(root, DATABASE_DIRECTORY_NAME);
    }

    private static void initDB() {
        DB_DUMP_FILE = new FileBasedFile(DB_DIRECTORY, DB_DUMP_FILE_NAME);
        if (!DB_DUMP_FILE.exists() && DB_DIRECTORY.mkdirs()) {
            DB_DUMP_FILE.toFile();
        }
    }

    private static void loadSOSNode() throws ConfigurationException {
        try (BufferedReader reader = new BufferedReader
                (new FileReader(SOS_ROOT + NODE_FILE)) ){
            String nodeIdString = reader.readLine();
            // TODO - get role
            if (nodeIdString != null && !nodeIdString.isEmpty()) {
                IGUID guid = GUIDFactory.recreateGUID(nodeIdString);

                node = new SOSNode(guid); // TODO - set address and roles
            }
        } catch (IOException | GUIDGenerationException e) {
            throw new ConfigurationException();
        }
    }

}

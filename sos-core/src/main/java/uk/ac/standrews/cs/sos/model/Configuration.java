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

    private static final String TEST_DATA_DIRECTORY_NAME = "test_data";
    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String INDEX_DIRECTORY_NAME = "index";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String KEYS_DIRECTORY_NAME = "keys";
    private static final String DATABASE_DIRECTORY_NAME = "db";

    private static final String NODE_FILE = "node.txt";
    private static final String PRIVATE_KEY_FILE = "private.der";
    private static final String PUBLIC_KEY_FILE = "public.der";
    private static final String DB_DUMP_FILE_NAME = "test.db";

    private static SOSDirectory TEST_DATA_DIRECTORY;
    private static SOSDirectory DATA_DIRECTORY;
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
            initRootName();
            initDirectories();
            initDB();
            loadSOSNode();

            instance = new Configuration();
        }

        return instance;
    }

    /**
     * Set the rootname if this has not been set already.
     * @param rootName
     */
    public static void setRootName(String rootName) {
        if (Configuration.rootName == null) {
            Configuration.rootName = rootName;
        }
    }

    /**
     * Get the node for this configuration.
     *
     * @return
     */
    public Node getNode() {
     return node;
    }

    /**
     * Set a node for this configuration if one does not exist.
     * @param node
     * @throws ConfigurationException
     */
    public void setNode(Node node) throws ConfigurationException {
        if (Configuration.node == null) {
            Configuration.node = node;
            saveConfiguration();
        }
    }

    public byte getRoles() {
        return 0; // TODO
    }

    /**
     * Get the directory for the test data.
     * @return
     */
    public SOSDirectory getTestDataDirectory() {
        return TEST_DATA_DIRECTORY;
    }

    /**
     * Get the directory for the manifests.
     * @return
     */
    public SOSDirectory getManifestsDirectory() {
        return MANIFEST_DIRECTORY;
    }

    /**
     * Get the key files for this node.
     * @return
     */
    public SOSFile[] getIdentityPaths() {
         return new SOSFile[] { new FileBasedFile(KEYS_DIRECTORY, PRIVATE_KEY_FILE),
                 new FileBasedFile(KEYS_DIRECTORY, PUBLIC_KEY_FILE) };
    }

    /**
     * Get the directory containing the index files.
     * @return
     */
    public SOSDirectory getIndexDirectory() {
         return INDEX_DIRECTORY;
    }

    /**
     * Get the directory containing the data of this node.
     * @return
     */
    public SOSDirectory getDataDirectory() {
        return DATA_DIRECTORY;
    }

    /**
     * Get the internal DB dump.
     * @return
     */
    public SOSFile getDatabaseDump() {
        return DB_DUMP_FILE;
    }

    /**
     * Save the configuration for this node.
     * @throws ConfigurationException
     */
    public void saveConfiguration() throws ConfigurationException {
        try (BufferedWriter writer = new BufferedWriter
                (new FileWriter(SOS_ROOT + NODE_FILE)) ){
            if (node != null) {
                writer.write(node.toString());
            }
        } catch (IOException e) {
            throw new ConfigurationException();
        }

        // TODO - save root name
    }

    private static void initRootName() {
        if (Configuration.rootName == null) {
            Configuration.rootName = DEFAULT_ROOT_NAME;
        }
    }

    private static void initDirectories() {
        SOSDirectory root = new FileBasedDirectory(ROOT_DIRECTORY, Configuration.rootName);
        TEST_DATA_DIRECTORY = new FileBasedDirectory(root, TEST_DATA_DIRECTORY_NAME);
        DATA_DIRECTORY = new FileBasedDirectory(root, DATA_DIRECTORY_NAME);
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
            // TODO - get role, rootname
            if (nodeIdString != null && !nodeIdString.isEmpty()) {
                IGUID guid = GUIDFactory.recreateGUID(nodeIdString);

                node = new SOSNode(guid); // TODO - set address and roles
            }
        } catch (IOException | GUIDGenerationException e) {
            throw new ConfigurationException();
        }
    }

}

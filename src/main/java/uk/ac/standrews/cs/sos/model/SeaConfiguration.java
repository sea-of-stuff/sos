package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedDirectory;

import java.io.*;

/**
 * FIXME - this should make use of the interfaces in storage
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaConfiguration {

    private static final String HOME = System.getProperty("user.home") + "/";
    private static final String SOS_ROOT = HOME + "sos/";
    private static final String DEFAULT_ROOT_NAME = "";

    private static final SOSDirectory ROOT_DIRECTORY = new FileBasedDirectory(HOME + "sos"); // FIXME - this should be equivalent to String root

    private static final String SOS_NODE_CONFIG = "node.txt";
    private static final String DATA_CONFIG = "config.txt";

    private static final String DATA_FOLDER = "data/";
    private static final String CACHE_FOLDER = "cached_data/";

    private static final SOSDirectory INDEX_DIRECTORY = new FileBasedDirectory(ROOT_DIRECTORY, "index");
    private static final SOSDirectory MANIFEST_DIRECTORY = new FileBasedDirectory(ROOT_DIRECTORY, "manifests");

    private static final String KEYS_FOLDER = "keys/";
    private static final String DB_FOLDER = "db/";

    private static final String PRIVATE_KEY_FILE = "private.der";
    private static final String PUBLIC_KEY_FILE = "public.der";

    private static IGUID nodeId;
    private static String privateKeyFile;
    private static String publicKeyFile;

    private static SeaConfiguration instance;
    private static String root;
    private static String rootName;

    public static void setRootName(String rootName) {
        if (SeaConfiguration.rootName == null) {
            SeaConfiguration.rootName = rootName;
        }
    }

    public static SeaConfiguration getInstance() throws SeaConfigurationException {
        if(instance == null) {
            if (SeaConfiguration.rootName == null) {
                SeaConfiguration.rootName = DEFAULT_ROOT_NAME;
            }
            root = SOS_ROOT + SeaConfiguration.rootName + "/";

            loadSOSNode();
            loadConfiguration();
            instance = new SeaConfiguration();
        }
        return instance;
    }

    private static void loadSOSNode() throws SeaConfigurationException {
        try (BufferedReader reader = new BufferedReader
                (new FileReader(SOS_ROOT + SOS_NODE_CONFIG)) ){
            String nodeIdString = reader.readLine();
            if (nodeIdString != null && !nodeIdString.isEmpty()) {
                nodeId = GUIDFactory.recreateGUID(nodeIdString);
            }
        } catch (IOException | GUIDGenerationException e) {
            throw new SeaConfigurationException();
        }
    }

    private static void loadConfiguration() {
        try (BufferedReader reader = new BufferedReader
                (new FileReader(SOS_ROOT + DATA_CONFIG)) ){
            privateKeyFile = reader.readLine();
            publicKeyFile =  reader.readLine();

        } catch (IOException e) {
            // FIXME - check if file exist, if it does read otherwise write.
            if (privateKeyFile == null) {
                privateKeyFile = PRIVATE_KEY_FILE;
            }

            if (publicKeyFile == null) {
                publicKeyFile = PUBLIC_KEY_FILE;
            }
        }
    }

    public IGUID getNodeId() {
     return nodeId;
    }

    public void setNodeId(IGUID nodeId) throws SeaConfigurationException {
        if (SeaConfiguration.nodeId == null) {
            SeaConfiguration.nodeId = nodeId;
            saveConfiguration();
        }
    }

    public String getDataPath() {
        return root + DATA_FOLDER;
    }

    public SOSDirectory getManifestsDirectory() {
        return MANIFEST_DIRECTORY;
    }

    public String[] getIdentityPaths() {
         return new String[] { root + KEYS_FOLDER + privateKeyFile,
                 root + KEYS_FOLDER + publicKeyFile };
    }

    public SOSDirectory getIndexPath() {
         return INDEX_DIRECTORY;
    }

    public String getCacheDataPath() {
        return root + CACHE_FOLDER;
    }

    public String getDBFolder() {
        return root + DB_FOLDER; // TODO - make directory if this does not exist
    }

    public void saveConfiguration() throws SeaConfigurationException {

        try (BufferedWriter writer = new BufferedWriter
                (new FileWriter(SOS_ROOT + SOS_NODE_CONFIG)) ){
            if (nodeId != null) {
                writer.write(nodeId.toString());
            }
        } catch (IOException e) {
            throw new SeaConfigurationException();
        }
    }

}

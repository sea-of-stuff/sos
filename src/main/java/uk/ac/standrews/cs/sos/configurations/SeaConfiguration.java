package uk.ac.standrews.cs.sos.configurations;

import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaConfiguration {

    private static final String HOME = System.getProperty("user.home") + "/";
    private static final String SOS_ROOT = HOME + "sos/";
    private static final String DEFAULT_ROOT_NAME = "";
    private static final String SOS_NODE_CONFIG = "node.txt";
    private static final String DATA_CONFIG = "config.txt";

    private static final String DATA_FOLDER = "data/";
    private static final String CACHE_FOLDER = "cached_data/";
    private static final String INDEX_FOLDER = "index/";
    private static final String MANIFEST_FOLDER = "manifests/";
    private static final String KEYS_FOLDER = "keys/";

    private static final String PRIVATE_KEY_FILE = "private.der";
    private static final String PUBLIC_KEY_FILE = "public.der";

    private static GUID nodeId;
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

    public static SeaConfiguration getInstance() throws IOException {
        if(instance == null) {
            if (SeaConfiguration.rootName == null) {
                SeaConfiguration.rootName = DEFAULT_ROOT_NAME;
            }
            root = SOS_ROOT + SeaConfiguration.rootName + "/";

            try {
                loadSOSNode();
            } catch (SeaConfigurationException e) {
                e.printStackTrace();
            }

            try {
                loadConfiguration();
            } catch (SeaConfigurationException e) {
                e.printStackTrace();
            }

            instance = new SeaConfiguration();
        }
        return instance;
    }

    private static void loadSOSNode() throws SeaConfigurationException {
        try (BufferedReader reader = new BufferedReader
                (new FileReader(SOS_ROOT + SOS_NODE_CONFIG)) ){
            String nodeIdString = reader.readLine();
            if (nodeIdString != null && nodeIdString.isEmpty()) {
                nodeId = new GUIDsha1(nodeIdString);
            }
        } catch (IOException e) {
            throw new SeaConfigurationException();
        }
    }

    private static void loadConfiguration() throws SeaConfigurationException {
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

    public GUID getNodeId() {
     return nodeId;
    }

    public void setNodeId(GUID nodeId) throws SeaConfigurationException {
        if (SeaConfiguration.nodeId == null) {
            SeaConfiguration.nodeId = nodeId;
            saveConfiguration();
        }
    }

    public String getDataPath() {
        return root + DATA_FOLDER;
    }

    public String getLocalManifestsLocation() {
        return root + MANIFEST_FOLDER;
    }

    public String[] getIdentityPaths() {
         return new String[] { root + KEYS_FOLDER + privateKeyFile,
                 root + KEYS_FOLDER + publicKeyFile };
    }

    public String getIndexPath() {
         return root + INDEX_FOLDER;
    }

    public String getCacheDataPath() {
        return root + CACHE_FOLDER;
    }

    public void saveConfiguration() throws SeaConfigurationException {

        try (BufferedWriter writer = new BufferedWriter
                (new FileWriter(root + SOS_NODE_CONFIG)) ){
            if (nodeId != null) {
                writer.write(nodeId.toString());
            }
        } catch (IOException e) {
            throw new SeaConfigurationException();
        }
    }

}

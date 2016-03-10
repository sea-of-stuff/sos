package uk.ac.standrews.cs.sos.configurations;

import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaConfiguration {

    private static final String HOME = System.getProperty("user.home");
    private static final String SOS_ROOT = "sos/";
    private static final String SOS_NODE_CONFIG = "node.txt";
    private static final String DATA_CONFIG = "config.txt";
    private static final String DEFAULT_ROOT_NAME = "";

    private static GUID nodeId;
    private static String root;
    private static String data;
    private static String cachedData;
    private static String index;
    private static String manifests;
    private static String privateKeyFile;
    private static String publicKeyFile;

    // XXX - other configs, such as #threads running, etc, could be useful

    private static SeaConfiguration instance;
    private static String rootName;

    public static void setRootName(String rootName) {
        if (SeaConfiguration.rootName != null) {
            SeaConfiguration.rootName = rootName;
        }
    }

    public static SeaConfiguration getInstance() throws IOException {
        if(instance == null) {
            if (SeaConfiguration.rootName == null) {
                SeaConfiguration.rootName = DEFAULT_ROOT_NAME;
            }

            try {
                loadSOSNode();

                root = HOME + SOS_ROOT + rootName + "/";
                loadConfiguration();
            } catch (SeaConfigurationException e) {
                throw new IOException();
            }
            instance = new SeaConfiguration();
        }
        return instance;
    }

    private static void loadSOSNode() throws SeaConfigurationException {
        try (BufferedReader reader = new BufferedReader
                (new FileReader(HOME + SOS_ROOT + SOS_NODE_CONFIG)) ){
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
                (new FileReader(HOME + SOS_ROOT + DATA_CONFIG)) ){
            data = "data/";
            cachedData = "cached_data/";
            index = "index/";
            manifests = "manifests/";
            privateKeyFile = "keys/" + reader.readLine();
            publicKeyFile = "keys/" + reader.readLine();
        } catch (IOException e) {
            throw new SeaConfigurationException();
        }
    }

    public GUID getNodeId() {
     return nodeId;
    }

    public void setNodeId(GUID nodeId) {
        if (SeaConfiguration.nodeId == null) {
            SeaConfiguration.nodeId = nodeId;
        }
    }

    public String getDataPath() {
        return root + data;
    }

    public String getLocalManifestsLocation() {
        return root + manifests;
    }

    public String[] getIdentityPaths() {
         return new String[] { root + privateKeyFile,
                 root + publicKeyFile };
    }

    public String getIndexPath() {
         return root + index;
    }

    public String getCacheDataPath() {
        return root + cachedData;
    }


}

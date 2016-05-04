package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedDirectory;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;
import uk.ac.standrews.cs.sos.network.Node;
import uk.ac.standrews.cs.sos.network.SOSNode;

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
    private static final String PRIVATE_KEY_FILE = "private.der";
    private static final String PUBLIC_KEY_FILE = "public.der";

    private static SOSDirectory DATA_DIRECTORY;
    private static SOSDirectory CACHE_DIRECTORY;
    private static SOSDirectory INDEX_DIRECTORY;
    private static SOSDirectory MANIFEST_DIRECTORY;
    private static SOSDirectory KEYS_DIRECTORY;
    private static SOSDirectory DB_DIRECTORY;

    private static Node node;
    private static String privateKeyFile;
    private static String publicKeyFile;

    private static SeaConfiguration instance;
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
            SOSDirectory root = new FileBasedDirectory(ROOT_DIRECTORY, SeaConfiguration.rootName);

            DATA_DIRECTORY = new FileBasedDirectory(root, "data");
            CACHE_DIRECTORY = new FileBasedDirectory(root, "cached_data");
            INDEX_DIRECTORY = new FileBasedDirectory(root, "index");
            MANIFEST_DIRECTORY = new FileBasedDirectory(root, "manifests");
            KEYS_DIRECTORY = new FileBasedDirectory(root, "keys");
            DB_DIRECTORY = new FileBasedDirectory(root, "db");

            DB_DIRECTORY.mkdirs();

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
            // TODO - get role
            if (nodeIdString != null && !nodeIdString.isEmpty()) {
                IGUID guid = GUIDFactory.recreateGUID(nodeIdString);

                node = new SOSNode(guid); // TODO - set address and roles
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

    public Node getNode() {
     return node;
    }

    public void setNode(Node node) throws SeaConfigurationException {
        if (SeaConfiguration.node == null) {
            SeaConfiguration.node = node;
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
         return new SOSFile[] { new FileBasedFile(KEYS_DIRECTORY, privateKeyFile),
                 new FileBasedFile(KEYS_DIRECTORY, publicKeyFile) };
    }

    public SOSDirectory getIndexDirectory() {
         return INDEX_DIRECTORY;
    }

    public SOSDirectory getCacheDirectory() {
        return CACHE_DIRECTORY;
    }

    public SOSDirectory getDBDirectory() {
        return DB_DIRECTORY;
    }

    public void saveConfiguration() throws SeaConfigurationException {

        try (BufferedWriter writer = new BufferedWriter
                (new FileWriter(SOS_ROOT + SOS_NODE_CONFIG)) ){
            if (node != null) {
                writer.write(node.toString());
            }
        } catch (IOException e) {
            throw new SeaConfigurationException();
        }
    }

}

package uk.ac.standrews.cs.sos.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.node.database.DatabaseType;
import uk.ac.standrews.cs.sos.policy.BasicManifestPolicy;
import uk.ac.standrews.cs.sos.policy.BasicMetadataPolicy;
import uk.ac.standrews.cs.sos.policy.BasicReplicationPolicy;
import uk.ac.standrews.cs.sos.policy.PolicyManagerImpl;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.StorageType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility class allows the SOS-Core instance to read a configuration file.
 * The read configuration will be used to create a custom sos instance.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSConfiguration {

    private Config configuration;
    private File file;

    /**
     * Create a configuration using the specified file (must be accessibly locally)
     * @param file
     */
    public SOSConfiguration(File file) throws SOSConfigurationException {
        this.file = file;

        configuration = ConfigFactory.parseFile(file);
    }

    public IGUID getNodeGUID() throws IOException, GUIDGenerationException {
        String randomGuid = GUIDFactory.generateRandomGUID().toString();

        String guidString = randomGuid;
        try {
            guidString = configuration.getString(PropertyKeys.NODE_GUID);
            if (useRandomGUID(guidString, randomGuid)) {
                setProperty(PropertyKeys.NODE_GUID, randomGuid);
                guidString = randomGuid;
            }

        } catch (ConfigException.Missing missing) {
            SOS_LOG.log(LEVEL.INFO, "GUID missing, use random GUID");
        }

        return GUIDFactory.recreateGUID(guidString);
    }

    private boolean useRandomGUID(String guid, String random) {
        if (guid.isEmpty())
            return true;

        if (guid.equals(random)) {
            return true;
        }

        // Check if GUID is valid
        try {
            GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return true;
        }

        return false;
    }

    public String getNodeHostname() {
        return configuration.getString(PropertyKeys.NODE_HOSTNAME);
    }

    public int getNodePort() {
        return configuration.getInt(PropertyKeys.NODE_PORT);
    }

    public boolean nodeIsAgent() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_AGENT);
    }

    public boolean nodeIsStorage() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_STORAGE);
    }

    public boolean nodeIsDDS() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_DDS);
    }

    public boolean nodeIsNDS() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_NDS);
    }

    public boolean nodeIsMCS() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_MCS);
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

        String path = configuration.getString(PropertyKeys.STORAGE_LOCATION);

        if (path.charAt(0) == '~') {
            path = System.getProperty("user.home") + path.substring(1);
        }

        return path;
    }

    public String getKeyFolderPath() {
        String path = configuration.getString(PropertyKeys.KEYS_FOLDER);

        if (path.charAt(0) == '~') {
            path = System.getProperty("user.home") + path.substring(1);
        }

        return path;
    }

    public PolicyManager getPolicyManager() {

        ReplicationPolicy replicationPolicy = createReplicationPolicy();
        ManifestPolicy manifestPolicy = createManifestPolicy();
        MetadataPolicy metadataPolicy = createMetadataPolicy();

        PolicyManager policyManager = new PolicyManagerImpl();
        policyManager.setReplicationPolicy(replicationPolicy);
        policyManager.setManifestPolicy(manifestPolicy);
        policyManager.setMetadataPolicy(metadataPolicy);

        return policyManager;
    }

    private ReplicationPolicy createReplicationPolicy() {

        int replicationFactor = configuration.getInt(PropertyKeys.POLICY_REPLICATION_FACTOR);

        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(replicationFactor);
        return replicationPolicy;
    }

    private ManifestPolicy createManifestPolicy() {

        boolean storeLocally = configuration.getBoolean(PropertyKeys.POLICY_MANIFEST_LOCALLY);
        boolean storeRemotely = configuration.getBoolean(PropertyKeys.POLICY_MANIFEST_REMOTELY);
        int replicationFactor = configuration.getInt(PropertyKeys.POLICY_MANIFEST_REPLICATION);

        ManifestPolicy manifestPolicy = new BasicManifestPolicy(storeLocally, storeRemotely, replicationFactor);
        return manifestPolicy;
    }

    private MetadataPolicy createMetadataPolicy() {
        MetadataPolicy metadataPolicy = new BasicMetadataPolicy();

        return metadataPolicy;
    }

    public List<Node> getBootstrapNodes() throws GUIDGenerationException {

        List<? extends Config> bootstrap = configuration.getConfigList(PropertyKeys.BOOTSTRAP_NODES);

        List<Node> bootstrapNodes = new ArrayList<>();
        for(Config bootstrapConfig : bootstrap) {
            Node node = getNode(bootstrapConfig);
            bootstrapNodes.add(node);
        }

        return bootstrapNodes;
    }

    private Node getNode(Config config) throws GUIDGenerationException {
        String guidString = config.getString("guid");
        IGUID guid = GUIDFactory.recreateGUID(guidString);

        String hostname = config.getString("hostname");
        int port = config.getInt("port");

        boolean isAgent = config.getBoolean("is.agent");
        boolean isStorage = config.getBoolean("is.storage");
        boolean isDDS = config.getBoolean("is.dds");
        boolean isNDS = config.getBoolean("is.nds");
        boolean isMCS = config.getBoolean("is.mcs");

        Node node = new SOSNode(guid, hostname, port, isAgent, isStorage, isDDS, isNDS, isMCS);
        return node;
    }

    private void setProperty(String key, String value) throws IOException {
        configuration = configuration.withValue(key, ConfigValueFactory.fromAnyRef(value));

        try(PrintWriter out = new PrintWriter(file)){
            out.println(configuration.root().render());
        }
    }

    private class PropertyKeys {

        public static final String NODE_GUID = "node.guid";
        public static final String NODE_HOSTNAME = "node.hostname";
        public static final String NODE_PORT = "node.port";
        public static final String NODE_IS_AGENT = "node.is.agent";
        public static final String NODE_IS_STORAGE = "node.is.storage";
        public static final String NODE_IS_DDS = "node.is.dds";
        public static final String NODE_IS_NDS = "node.is.nds";
        public static final String NODE_IS_MCS = "node.is.mcs";

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

        public static final String POLICY_REPLICATION_FACTOR = "policy.replication.factor";
        public static final String POLICY_MANIFEST_LOCALLY = "policy.manifest.locally";
        public static final String POLICY_MANIFEST_REMOTELY = "policy.manifest.remotely";
        public static final String POLICY_MANIFEST_REPLICATION = "policy.manifest.replication";

        public static final String BOOTSTRAP_NODES = "bootstrap";
    }
}

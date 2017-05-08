package uk.ac.standrews.cs.sos.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

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

    private static final char HOME_SYMBOL = '~';
    private static final String HOME_PATH = System.getProperty("user.home");
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

    public boolean nodeIsMMS() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_MMS);
    }

    public boolean nodeIsCMS() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_CMS);
    }

    public boolean nodeIsRMS() {
        return configuration.getBoolean(PropertyKeys.NODE_IS_RMS);
    }

    public String getDBFilename() {
        return configuration.getString(PropertyKeys.DB_FILENAME);
    }

    public CastoreBuilder getCastoreBuilder() {

        CastoreType storageType = CastoreType.getEnum(configuration.getString(PropertyKeys.STORAGE_TYPE));
        String root = getStorageLocation();
        return new CastoreBuilder()
                .setType(storageType)
                .setRoot(root);
    }

    public String getStorageLocation() {
        String path = configuration.getString(PropertyKeys.STORAGE_LOCATION);
        return absolutePath(path);
    }

    public String getKeyFolderPath() {
        String path = configuration.getString(PropertyKeys.KEYS_FOLDER);
        return absolutePath(path);
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
        String guidString = config.getString(PropertyKeys.BOOTSTRAP_NODE_GUID);
        IGUID guid = GUIDFactory.recreateGUID(guidString);

        String hostname = config.getString(PropertyKeys.BOOTSTRAP_NODE_HOSTNAME);
        int port = config.getInt(PropertyKeys.BOOTSTRAP_NODE_PORT);

        boolean isAgent = config.getBoolean(PropertyKeys.BOOTSTRAP_NODE_IS_AGENT);
        boolean isStorage = config.getBoolean(PropertyKeys.BOOTSTRAP_NODE_IS_STORAGE);
        boolean isDDS = config.getBoolean(PropertyKeys.BOOTSTRAP_NODE_IS_DDS);
        boolean isNDS = config.getBoolean(PropertyKeys.BOOTSTRAP_NODE_IS_NDS);
        boolean isMMS = config.getBoolean(PropertyKeys.BOOTSTRAP_NODE_IS_MMS);
        boolean isCMS = config.getBoolean(PropertyKeys.BOOTSTRAP_NODE_IS_MMS);
        boolean isRMS = config.getBoolean(PropertyKeys.BOOTSTRAP_NODE_IS_MMS);

        return new SOSNode(guid, hostname, port, isAgent, isStorage, isDDS, isNDS, isMMS, isCMS, isRMS);
    }

    public int getWebDAVPort() {
        return configuration.getInt(PropertyKeys.WEBDAV_PORT);
    }

    public int getWebAppPort() {
        return configuration.getInt(PropertyKeys.WEBAPP_PORT);
    }

    private void setProperty(String key, String value) throws IOException {
        configuration = configuration.withValue(key, ConfigValueFactory.fromAnyRef(value));

        try(PrintWriter out = new PrintWriter(file)){
            out.println(configuration.root().render());
        }
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

    private String absolutePath(String path) {
        if (path.charAt(0) == HOME_SYMBOL) {
            path = HOME_PATH + path.substring(1); // Skip HOME_SYMBOL
        }

        return path;
    }

    private class PropertyKeys {

        static final String NODE_GUID = "node.guid";
        static final String NODE_PORT = "node.port";
        static final String NODE_IS_AGENT = "node.is.agent";
        static final String NODE_IS_STORAGE = "node.is.storage";
        static final String NODE_IS_DDS = "node.is.dds";
        static final String NODE_IS_NDS = "node.is.nds";
        static final String NODE_IS_MMS = "node.is.mms";
        static final String NODE_IS_CMS = "node.is.cms";
        static final String NODE_IS_RMS = "node.is.rms";

        static final String DB_FILENAME = "db.filename";

        static final String STORAGE_TYPE = "storage.type";
        static final String STORAGE_HOSTNAME = "storage.hostname";
        static final String STORAGE_LOCATION = "storage.location";
        static final String STORAGE_USERNAME = "storage.username";
        static final String STORAGE_PASSWORD = "storage.password";
        static final String STORAGE_ACCESS_KEY = "storage.access.key";
        static final String STORAGE_SECRET_KEY = "storage.secret.key";

        static final String KEYS_FOLDER = "keys.folder";

        static final String BOOTSTRAP_NODES = "bootstrap";
        static final String BOOTSTRAP_NODE_GUID = "guid";
        static final String BOOTSTRAP_NODE_HOSTNAME = "hostname";
        static final String BOOTSTRAP_NODE_PORT = "port";
        static final String BOOTSTRAP_NODE_IS_AGENT = "is.agent";
        static final String BOOTSTRAP_NODE_IS_STORAGE = "is.storage";
        static final String BOOTSTRAP_NODE_IS_DDS = "is.dds";
        static final String BOOTSTRAP_NODE_IS_NDS = "is.nds";
        static final String BOOTSTRAP_NODE_IS_MMS = "is.mms";
        static final String BOOTSTRAP_NODE_IS_CMS = "is.cms";
        static final String BOOTSTRAP_NODE_IS_RMS = "is.rms";

        static final String WEBDAV_PORT = "webdav.port";
        static final String WEBAPP_PORT = "webapp.port";

        static final String FS_ROOT_GUID = "fs.root.guid";

        static final String CACHE_FLUSHER_FREQUENCY = "cache.flusher.frequency"; // in minutes
        static final String CACHE_MAX_SIZE = "cache.max.size"; // in MB
    }
}

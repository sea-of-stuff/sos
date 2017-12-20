package uk.ac.standrews.cs.sos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import javax.ws.rs.NotSupportedException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SettingsConfiguration {

    public static final char HOME_SYMBOL = '~';
    public static final String HOME_PATH = System.getProperty("user.home");

    @JsonIgnore
    private JsonNode node;

    public SettingsConfiguration() {}

    public SettingsConfiguration(File file) throws ConfigurationException {
        try {
            node = JSONHelper.jsonObjMapper().readTree(file);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration properly", e);
        }
    }

    public Settings getSettingsObj() {

        return JSONHelper.jsonObjMapper().convertValue(node, SettingsConfiguration.class).getSettings();
    }

    public static String absolutePath(String path) {
        if (path.charAt(0) == HOME_SYMBOL) {
            path = HOME_PATH + path.substring(1); // Skip HOME_SYMBOL
        }

        return path;
    }

    // POJO for JACKSON
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    ///////////////////////////////////////
    // POJO for Jackson serialisation /////
    ///////////////////////////////////////
    public static class Settings {

        @JsonIgnore
        private String guid; // The local node uses this field to share its GUID across different parts of the node.

        private AdvanceServicesSettings services;
        private DatabaseSettings database;
        private RESTSettings rest;
        private WebDAVSettings webDAV;
        private WebAPPSettings webAPP;
        private KeysSettings keys;
        private StoreSettings store;
        private GlobalSettings global;
        private List<SimpleNode> bootstrapNodes;

        public Settings() {}

        @JsonIgnore
        public String getGuid() {
            return guid;
        }

        @JsonIgnore
        public void setGuid(String guid) {
            this.guid = guid;
        }

        @JsonIgnore
        public IGUID guid() {

            try {
                return GUIDFactory.recreateGUID(getGuid());
            } catch (GUIDGenerationException e) {
                return new InvalidID();
            }
        }

        public AdvanceServicesSettings getServices() {
            return services;
        }

        public void setServices(AdvanceServicesSettings services) {
            this.services = services;
        }

        public DatabaseSettings getDatabase() {
            return database;
        }

        public void setDatabase(DatabaseSettings database) {
            this.database = database;
        }

        public RESTSettings getRest() {
            return rest;
        }

        public void setRest(RESTSettings rest) {
            this.rest = rest;
        }

        public WebDAVSettings getWebDAV() {
            return webDAV;
        }

        public void setWebDAV(WebDAVSettings webDAV) {
            this.webDAV = webDAV;
        }

        public WebAPPSettings getWebAPP() {
            return webAPP;
        }

        public void setWebAPP(WebAPPSettings webAPP) {
            this.webAPP = webAPP;
        }

        public KeysSettings getKeys() {
            return keys;
        }

        public void setKeys(KeysSettings keys) {
            this.keys = keys;
        }

        public StoreSettings getStore() {
            return store;
        }

        public void setStore(StoreSettings store) {
            this.store = store;
        }

        public GlobalSettings getGlobal() {
            return global;
        }

        public void setGlobal(GlobalSettings global) {
            this.global = global;
        }

        public List<SimpleNode> getBootstrapNodes() {
            return bootstrapNodes;
        }

        public void setBootstrapNodes(List<SimpleNode> bootstrapNodes) {
            this.bootstrapNodes = bootstrapNodes;
        }

        public static class SimpleNode extends BasicManifest implements Node {

            private String guid;
            private String hostname; // or ip
            private int port = 8080;
            private String signCert;

            protected SimpleNode() {
                super(ManifestType.NODE);
            }

            public String getGuid() {
                return guid;
            }

            public void setGuid(String guid) {
                this.guid = guid;
            }

            @JsonIgnore
            @Override
            public IGUID guid() {
                try {
                    return GUIDFactory.recreateGUID(getGuid());
                } catch (GUIDGenerationException e) {
                    return new InvalidID();
                }
            }

            public String getIP() {
                return hostname;
            }

            public void setHostname(String hostname) {
                this.hostname = hostname;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public String getSignCert() {
                return signCert;
            }

            public void setSignCert(String signCert) {
                this.signCert = signCert;
            }

            @JsonIgnore
            public PublicKey getSignatureCertificate() {

                try {
                    return DigitalSignature.getCertificate(getSignCert());
                } catch (CryptoException e) {
                    throw new Error("Settings - certificate for bootstrap node is invalid");
                }
            }

            @JsonIgnore
            @Override
            public InetSocketAddress getHostAddress() {
                return new InetSocketAddress(getIP(), getPort());
            }

            // IGNORE METHODS BELOW

            @JsonIgnore
            @Override
            public boolean isAgent() {
                return false;
            }

            @JsonIgnore
            @Override
            public boolean isStorage() {
                return false;
            }

            @JsonIgnore
            @Override
            public boolean isDDS() {
                return false;
            }

            @JsonIgnore
            @Override
            public boolean isNDS() {
                return false;
            }

            @JsonIgnore
            @Override
            public boolean isMMS() {
                return false;
            }

            @JsonIgnore
            @Override
            public boolean isCMS() {
                return false;
            }

            @JsonIgnore
            @Override
            public boolean isRMS() {
                return false;
            }

            @JsonIgnore
            @Override
            public boolean isExperiment() {
                return false;
            }

            @Override
            public InputStream contentToHash() {
                throw new NotSupportedException();
            }
        }

        // Settings relative to each Service
        public static class AdvanceServicesSettings {

            private RoleSettings agent = new RoleSettings();
            private StorageSettings storage = new StorageSettings(); // default
            private DDSSettings dds = new DDSSettings(); // default
            private NDSSettings nds = new NDSSettings(); // default
            private MMSSettings mms = new MMSSettings();
            private CMSSettings cms = new CMSSettings();
            private RMSSettings rms = new RMSSettings();
            private RoleSettings experiment = new RoleSettings();

            public AdvanceServicesSettings() {}

            public StorageSettings getStorage() {
                return storage;
            }

            public void setStorage(StorageSettings storage) {
                this.storage = storage;
            }

            public CMSSettings getCms() {
                return cms;
            }

            public void setCms(CMSSettings cms) {
                this.cms = cms;
            }

            public DDSSettings getDds() {
                return dds;
            }

            public void setDds(DDSSettings dds) {
                this.dds = dds;
            }

            public RMSSettings getRms() {
                return rms;
            }

            public void setRms(RMSSettings rms) {
                this.rms = rms;
            }

            public NDSSettings getNds() {
                return nds;
            }

            public void setNds(NDSSettings nds) {
                this.nds = nds;
            }

            public MMSSettings getMms() {
                return mms;
            }

            public void setMms(MMSSettings mms) {
                this.mms = mms;
            }

            public RoleSettings getAgent() {
                return agent;
            }

            public void setAgent(RoleSettings agent) {
                this.agent = agent;
            }

            public RoleSettings getExperiment() {
                return experiment;
            }

            public void setExperiment(RoleSettings experiment) {
                this.experiment = experiment;
            }

            ///////////////////////////////
            ////// SETTING CLASSES ////////
            ///////////////////////////////

            public static class StorageSettings extends RoleSettings {

                @JsonView(Views.Public.class)
                private boolean canPersist = false;

                @JsonView(Views.Public.class)
                private int maxReplication = 1; // A value of 1 results in no replication, since this storage service itself will count as replication-1

                private boolean sequentialReplication = true;

                public StorageSettings() {}

                public boolean isCanPersist() {
                    return canPersist;
                }

                public int getMaxReplication() {
                    return maxReplication;
                }

                public void setMaxReplication(int maxReplication) {
                    this.maxReplication = maxReplication;
                }

                public boolean isSequentialReplication() {
                    return sequentialReplication;
                }

                public void setSequentialReplication(boolean sequentialReplication) {
                    this.sequentialReplication = sequentialReplication;
                }
            }

            public static class NDSSettings extends RoleSettings {

                private boolean startupRegistration = false;
                private boolean bootstrap = true;
                private boolean ping = false;

                public NDSSettings() {}

                public boolean isStartupRegistration() {
                    return startupRegistration;
                }

                public void setStartupRegistration(boolean startupRegistration) {
                    this.startupRegistration = startupRegistration;
                }

                public boolean isBootstrap() {
                    return bootstrap;
                }

                public void setBootstrap(boolean bootstrap) {
                    this.bootstrap = bootstrap;
                }

                public boolean isPing() {
                    return ping;
                }

                public void setPing(boolean ping) {
                    this.ping = ping;
                }
            }

            public static class MMSSettings extends RoleSettings {

                public MMSSettings() {}
            }

            public static class DDSSettings extends RoleSettings {

                @JsonView(Views.Public.class)
                private int maxReplication = 1; // A value of 1 results in no replication, since this dds service itself will count as replication-1

                private boolean sequentialReplication = true;

                public DDSSettings() {}

                public int getMaxReplication() {
                    return maxReplication;
                }

                public void setMaxReplication(int maxReplication) {
                    this.maxReplication = maxReplication;
                }

                public boolean isSequentialReplication() {
                    return sequentialReplication;
                }

                public void setSequentialReplication(boolean sequentialReplication) {
                    this.sequentialReplication = sequentialReplication;
                }
            }

            public static class RMSSettings extends RoleSettings {

                public RMSSettings() {}
            }

            public static class CMSSettings extends RoleSettings {

                private String loadedPath = "~/sos/java/contexts/"; // This is the default path

                // If true, the CMS will run background processes to classify content and maintain the contexts
                private boolean automatic = false;
                // If true the predicates are run just after adding the context
                private boolean predicateOnNewContext = false;
                private ThreadSettings predicateThread = new ThreadSettings();
                private ThreadSettings policiesThread = new ThreadSettings();
                private ThreadSettings checkPoliciesThread = new ThreadSettings();
                private ThreadSettings getdataThread = new ThreadSettings();
                private ThreadSettings spawnThread = new ThreadSettings();

                public CMSSettings() {}

                public boolean isAutomatic() {
                    return automatic;
                }

                public void setAutomatic(boolean automatic) {
                    this.automatic = automatic;
                }

                public ThreadSettings getPredicateThread() {
                    return predicateThread;
                }

                public void setPredicateThread(ThreadSettings predicateThread) {
                    this.predicateThread = predicateThread;
                }

                public ThreadSettings getPoliciesThread() {
                    return policiesThread;
                }

                public void setPoliciesThread(ThreadSettings policiesThread) {
                    this.policiesThread = policiesThread;
                }

                public ThreadSettings getGetdataThread() {
                    return getdataThread;
                }

                public void setGetdataThread(ThreadSettings getdataThread) {
                    this.getdataThread = getdataThread;
                }

                public ThreadSettings getSpawnThread() {
                    return spawnThread;
                }

                public void setSpawnThread(ThreadSettings spawnThread) {
                    this.spawnThread = spawnThread;
                }

                public ThreadSettings getCheckPoliciesThread() {
                    return checkPoliciesThread;
                }

                public void setCheckPoliciesThread(ThreadSettings checkPoliciesThread) {
                    this.checkPoliciesThread = checkPoliciesThread;
                }

                public String getLoadedPath() {
                    return absolutePath(loadedPath);
                }

                public void setLoadedPath(String loadedPath) {
                    this.loadedPath = loadedPath;
                }

                public boolean isPredicateOnNewContext() {
                    return predicateOnNewContext;
                }

                public void setPredicateOnNewContext(boolean predicateOnNewContext) {
                    this.predicateOnNewContext = predicateOnNewContext;
                }
            }

        }

        public static class RoleSettings {

            private boolean exposed = false;

            public RoleSettings() {}

            public boolean isExposed() {
                return exposed;
            }

            public void setExposed(boolean exposed) {
                this.exposed = exposed;
            }
        }

        public static class DatabaseSettings {

            private String filename;

            public DatabaseSettings() {}

            public String getFilename() {
                return filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }
        }

        public static class RESTSettings {

            private int port;

            public RESTSettings() {}

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }
        }

        public static class WebDAVSettings {

            private int port;

            public WebDAVSettings() {}

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }
        }

        public static class WebAPPSettings {

            private int port;

            public WebAPPSettings() {}

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }
        }

        public static class KeysSettings {

            private String location;

            public KeysSettings() {}

            public String getLocation() {
                return absolutePath(location);
            }

            public void setLocation(String location) {
                this.location = location;
            }
        }

        // These are the settings for the internal store.
        // The InternalStorage is used by multiple services and component to interact with the store of this node.
        // The InternalStorage differs from the Storage Actor
        public static class StoreSettings {

            private String type;
            private String location;

            public StoreSettings() {}

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getLocation() {
                return absolutePath(location);
            }

            public void setLocation(String location) {
                this.location = location;
            }

            // NOTE - only local CastoreStorage is supported at the moment.
            @JsonIgnore
            public CastoreBuilder getCastoreBuilder() {

                CastoreType storageType = CastoreType.getEnum(getType());
                String root = getLocation();

                return new CastoreBuilder()
                        .setType(storageType)
                        .setRoot(root);
            }
        }

        public static class GlobalSettings {

            private String ssl_trust_store;
            private TasksSettings tasks;
            private NodeMaintainerSettings nodeMaintainer;

            public GlobalSettings() {}

            public TasksSettings getTasks() {
                return tasks;
            }

            public void setTasks(TasksSettings tasks) {
                this.tasks = tasks;
            }

            public NodeMaintainerSettings getNodeMaintainer() {
                return nodeMaintainer;
            }

            public void setNodeMaintainer(NodeMaintainerSettings nodeMaintainer) {
                this.nodeMaintainer = nodeMaintainer;
            }

            public String getSsl_trust_store() {
                return ssl_trust_store;
            }

            public void setSsl_trust_store(String ssl_trust_store) {
                this.ssl_trust_store = ssl_trust_store;
                System.setProperty("javax.net.ssl.trustStore", ssl_trust_store);
            }

            public static class TasksSettings {

                private ThreadSettings thread;
                private boolean fallbackToSyncTasks = false;

                public TasksSettings() {}

                public ThreadSettings getThread() {
                    return thread;
                }

                public void setThread(ThreadSettings thread) {
                    this.thread = thread;
                }

                public boolean isFallbackToSyncTasks() {
                    return fallbackToSyncTasks;
                }

                public void setFallbackToSyncTasks(boolean fallbackToSyncTasks) {
                    this.fallbackToSyncTasks = fallbackToSyncTasks;
                }
            }

            public static class NodeMaintainerSettings extends ComponentSettings {

                private long maxSize; // in Bytes
                private ThreadSettings thread;

                public NodeMaintainerSettings() {}

                public ThreadSettings getThread() {
                    return thread;
                }

                public void setThread(ThreadSettings thread) {
                    this.thread = thread;
                }

                public long getMaxSize() {
                    return maxSize;
                }

                public void setMaxSize(long maxSize) {
                    this.maxSize = maxSize;
                }
            }
        }

        public static class ThreadSettings {

            private int ps = 1; // Number of threads
            private int initialDelay = 60;
            private int period = 60;
            // TODO - timeunit

            public ThreadSettings() {}

            public int getPs() {
                return ps;
            }

            public void setPs(int ps) {
                this.ps = ps;
            }

            public int getInitialDelay() {
                return initialDelay;
            }

            public void setInitialDelay(int initialDelay) {
                this.initialDelay = initialDelay;
            }

            public int getPeriod() {
                return period;
            }

            public void setPeriod(int period) {
                this.period = period;
            }
        }

        public static class ComponentSettings {

            private boolean enabled;

            public ComponentSettings() {}

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

    }

    public static class Views {
        public static class Public {
        }
    }
}

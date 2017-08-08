package uk.ac.standrews.cs.sos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
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
            node = JSONHelper.JsonObjMapper().readTree(file);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration properly");
        }
    }

    public Settings getSettingsObj() {

        return JSONHelper.JsonObjMapper().convertValue(node, SettingsConfiguration.class).getSettings();
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

        private String guid; // TODO - have a way to save the guid to the file if it is not provided
        private AdvanceServicesSettings services;
        private DatabaseSettings database;
        private RESTSettings rest;
        private WebDAVSettings webDAV;
        private WebAPPSettings webAPP;
        private KeysSettings keys;
        private StoreSettings store;
        private GlobalSettings global;
        private List<NodeSettings> bootstrapNodes;

        public Settings() {}

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        @JsonIgnore
        public IGUID getNodeGUID() {

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

        public List<NodeSettings> getBootstrapNodes() {
            return bootstrapNodes;
        }

        public void setBootstrapNodes(List<NodeSettings> bootstrapNodes) {
            this.bootstrapNodes = bootstrapNodes;
        }


        public static class NodeSettings implements Node {

            private String guid;
            private String hostname;
            private int port;
            private ServicesSettings services;

            public NodeSettings() {}

            @JsonIgnore
            public IGUID getNodeGUID() {

                try {
                    return GUIDFactory.recreateGUID(getGuid());
                } catch (GUIDGenerationException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @JsonIgnore
            public InetSocketAddress getHostAddress() {
                return new InetSocketAddress(getHostname(), getPort());
            }

            @Override
            public boolean isAgent() {
                return false;
            }

            @Override
            public boolean isStorage() {
                return services.getStorage().isExposed();
            }

            @Override
            public boolean isDDS() {
                return services.getDds().isExposed();
            }

            @Override
            public boolean isNDS() {
                return services.getNds().isExposed();
            }

            @Override
            public boolean isMMS() {
                return services.getMms().isExposed();
            }

            @Override
            public boolean isCMS() {
                return services.getCms().isExposed();
            }

            @Override
            public boolean isRMS() {
                return services.getRms().isExposed();
            }

            public String getHostname() {
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

            public String getGuid() {
                return guid;
            }

            public void setGuid(String guid) {
                this.guid = guid;
            }

            public ServicesSettings getServices() {
                return services;
            }

            public void setServices(ServicesSettings services) {
                this.services = services;
            }

            public static class ServicesSettings {

                private RoleSettings storage;
                private RoleSettings dds;
                private RoleSettings nds;
                private RoleSettings mms;
                private RoleSettings cms;
                private RoleSettings rms;

                public RoleSettings getStorage() {
                    return storage;
                }

                public void setStorage(RoleSettings storage) {
                    this.storage = storage;
                }

                public RoleSettings getDds() {
                    return dds;
                }

                public void setDds(RoleSettings dds) {
                    this.dds = dds;
                }

                public RoleSettings getNds() {
                    return nds;
                }

                public void setNds(RoleSettings nds) {
                    this.nds = nds;
                }

                public RoleSettings getMms() {
                    return mms;
                }

                public void setMms(RoleSettings mms) {
                    this.mms = mms;
                }

                public RoleSettings getCms() {
                    return cms;
                }

                public void setCms(RoleSettings cms) {
                    this.cms = cms;
                }

                public RoleSettings getRms() {
                    return rms;
                }

                public void setRms(RoleSettings rms) {
                    this.rms = rms;
                }

                public ServicesSettings() {}

            }

        }

        // Settings relative to each Service
        public static class AdvanceServicesSettings {

            private RoleSettings agent;
            private StorageSettings storage;
            private DDSSettings dds;
            private NDSSettings nds;
            private MMSSettings mms;
            private CMSSettings cms;
            private RMSSettings rms;

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

            ///////////////////////////////
            ////// SETTING CLASSES ////////
            ///////////////////////////////

            public static class StorageSettings extends RoleSettings {

                public StorageSettings() {}
            }

            public static class NDSSettings extends RoleSettings {

                private boolean startupRegistration;

                public NDSSettings() {}

                public boolean isStartupRegistration() {
                    return startupRegistration;
                }

                public void setStartupRegistration(boolean startupRegistration) {
                    this.startupRegistration = startupRegistration;
                }
            }

            public static class MMSSettings extends RoleSettings {

                public MMSSettings() {}
            }

            public static class DDSSettings extends RoleSettings {

                private String cacheFile;
                private String indexFile;

                public DDSSettings() {}

                public String getCacheFile() {
                    return cacheFile;
                }

                public void setCacheFile(String cacheFile) {
                    this.cacheFile = cacheFile;
                }

                public String getIndexFile() {
                    return indexFile;
                }

                public void setIndexFile(String indexFile) {
                    this.indexFile = indexFile;
                }
            }

            public static class RMSSettings extends RoleSettings {

                private String cacheFile;

                public RMSSettings() {}

                public String getCacheFile() {
                    return cacheFile;
                }

                public void setCacheFile(String cacheFile) {
                    this.cacheFile = cacheFile;
                }
            }

            public static class CMSSettings extends RoleSettings {

                private String indexFile;

                // If true, the CMS will run background processes to classify content and maintain the contexts
                private boolean automatic;
                private ThreadSettings predicateThread;
                private ThreadSettings policiesThread;
                private ThreadSettings checkPoliciesThread;
                private ThreadSettings getdataThread;
                private ThreadSettings spawnThread;

                public CMSSettings() {}

                public String getIndexFile() {
                    return indexFile;
                }

                public void setIndexFile(String indexFile) {
                    this.indexFile = indexFile;
                }

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
            }

        }

        public static class RoleSettings {

            private boolean exposed;

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
            public CastoreBuilder getCastoreBuilder() throws ConfigurationException {

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
            private CacheFlusherSettings cacheFlusher;

            public GlobalSettings() {}

            public TasksSettings getTasks() {
                return tasks;
            }

            public void setTasks(TasksSettings tasks) {
                this.tasks = tasks;
            }

            public CacheFlusherSettings getCacheFlusher() {
                return cacheFlusher;
            }

            public void setCacheFlusher(CacheFlusherSettings cacheFlusher) {
                this.cacheFlusher = cacheFlusher;
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

                public TasksSettings() {}

                public ThreadSettings getThread() {
                    return thread;
                }

                public void setThread(ThreadSettings thread) {
                    this.thread = thread;
                }
            }

            public static class CacheFlusherSettings extends ComponentSettings {

                private long maxSize; // in Bytes
                private ThreadSettings thread;

                public CacheFlusherSettings() {}

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

            private int ps; // Number of threads
            private int initialDelay;
            private int period;
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
}

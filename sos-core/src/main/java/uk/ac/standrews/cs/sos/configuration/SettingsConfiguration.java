package uk.ac.standrews.cs.sos.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.impl.keys.InvalidID;
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
        private AdvancedRolesSettings roles;
        private DatabaseSettings database;
        private RESTSettings rest;
        private WebDAVSettings webDAV;
        private WebAPPSettings webAPP;
        private KeysSettings keys;
        private StoreSettings store;
        private List<NodeSettings> bootstrapNodes;
        // TODO - policy settings
        // TODO - thread settings
        // TODO - turn on/off components of node

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

        public AdvancedRolesSettings getRoles() {
            return roles;
        }

        public void setRoles(AdvancedRolesSettings roles) {
            this.roles = roles;
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
            private RolesSettings roles;

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
                return roles.isAgent();
            }

            @Override
            public boolean isStorage() {
                return roles.isStorage();
            }

            @Override
            public boolean isDDS() {
                return roles.isDDS();
            }

            @Override
            public boolean isNDS() {
                return roles.isNDS();
            }

            @Override
            public boolean isMMS() {
                return roles.isMMS();
            }

            @Override
            public boolean isCMS() {
                return roles.isCMS();
            }

            @Override
            public boolean isRMS() {
                return roles.isRMS();
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

            public RolesSettings getRoles() {
                return roles;
            }

            public void setRoles(RolesSettings roles) {
                this.roles = roles;
            }

            public static class RolesSettings {

                private boolean agent;
                private boolean storage;
                private boolean dds;
                private boolean nds;
                private boolean mms;
                private boolean cms;
                private boolean rms;

                public RolesSettings() {}

                public boolean isAgent() {
                    return agent;
                }

                public void setAgent(boolean agent) {
                    this.agent = agent;
                }

                public boolean isStorage() {
                    return storage;
                }

                public void setStorage(boolean storage) {
                    this.storage = storage;
                }

                public boolean isDDS() {
                    return dds;
                }

                public void setDDS(boolean DDS) {
                    dds = DDS;
                }

                public boolean isNDS() {
                    return nds;
                }

                public void setNDS(boolean NDS) {
                    nds = NDS;
                }

                public boolean isMMS() {
                    return mms;
                }

                public void setMMS(boolean MMS) {
                    mms = MMS;
                }

                public boolean isCMS() {
                    return cms;
                }

                public void setCMS(boolean CMS) {
                    cms = CMS;
                }

                public boolean isRMS() {
                    return rms;
                }

                public void setRMS(boolean RMS) {
                    rms = RMS;
                }

            }

        }

        // Settings relative to each Role
        public static class AdvancedRolesSettings {

            private boolean storage;
            private boolean dds;
            private boolean nds;
            private boolean mms;
            private boolean cms;
            private boolean rms;

            public AdvancedRolesSettings() {}

            public static class StorageSettings {

                public StorageSettings() {}
            }

            public static class CMSSettings {

                private boolean automatic;

                // Thread scheduling properties
                // The first integer is the initial delay for the scheduler.
                // The second integer is the periodic delay for teh scheduler.
                private int[] predicate;
                private int[] policies;
                private int[] getdata;
                private int[] spawn;

                public CMSSettings() {}
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
        // The InternalStorage is used by multiple Actors and component to interact with the store of this node.
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

    }
}

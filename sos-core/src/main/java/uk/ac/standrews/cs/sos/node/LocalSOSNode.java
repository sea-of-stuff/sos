package uk.ac.standrews.cs.sos.node;

import com.j256.ormlite.support.ConnectionSource;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.node.SOSImpl.SOSClient;
import uk.ac.standrews.cs.sos.node.SOSImpl.SOSCoordinator;
import uk.ac.standrews.cs.sos.node.SOSImpl.SOSStorage;
import uk.ac.standrews.cs.sos.storage.StorageFactory;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.exceptions.StorageException;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class represents the SOSNode of this machine.
 * This node is a singleton.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalSOSNode extends SOSNode {

    private static Configuration configuration;
    private static Config config;
    private static Storage storage;

    private static Index index;
    private static Identity identity;
    private static ManifestsManager manifestsManager;
    private static NodeManager nodeManager;
    private static HashMap<ROLE, SeaOfStuff> sosMap;

    private static LocalSOSNode instance;
    private LocalSOSNode(Node node) {
        super(node);
    }

    /**
     * Create the LocalSOSNode.
     * The index must be already set using setIndex();
     *
     * @param configuration
     * @throws SOSException
     */
    public static void create(Configuration configuration) throws SOSException, SOSProtocolException {
        config = hardcodedConfiguration();
        try {
            storage = StorageFactory.createStorage(config.s_type, config.s_location, true);
        } catch (StorageException e) {
            throw new SOSException(e);
        }

        // TODO : index
        // TODO : readSystemProperties();

        checkRequisites();

        LocalSOSNode.configuration = configuration;
        instance = new LocalSOSNode(configuration.getNode());

        initManifestManager();
        initNodeManager();
        initIdentity();
        initSOSInstances();

        backgroundProcesses();
        registerSOSProtocol();
    }

    // REMOVEME - use system properties or info passed by user
    private static Config hardcodedConfiguration() {
        Config retval = null;

        Config.db_type = Config.DB_TYPE_SQLITE;
        try {
            Config.initDatabaseInfo();
        } catch (PersistenceException | IOException e) {
            e.printStackTrace();
        }

        try {
            ConnectionSource connection = SQLDB.getSQLConnection();
            retval = SQLDB.getConfiguration(connection);
        } catch (DatabasePersistenceException | SQLException e) {
            e.printStackTrace();
        }

        return retval;
    }

    // TODO - read system properties about db
    private static void readSystemProperties() {
        Config.db_path = "";
    }

    /**
     * Get the instance of this node.
     * @return
     * @throws SOSException if the manager was not created using the create() method
     */
    public static LocalSOSNode getInstance() throws SOSException {
        if (instance == null) {
            throw new SOSException();
        }

        return instance;
    }

    /**
     * Get a SeaOfStuff implementation.
     *
     * @param role
     * @return
     * @throws SOSException
     */
    public SeaOfStuff getSeaOfStuff(ROLE role) throws SOSException {
        if (instance != null && sosMap.containsKey(role)) {
            return sosMap.get(role);
        } else {
            throw new SOSException();
        }
    }

    /**
     * Returns true if the specified role is supported by this node.
     * @param role
     * @return
     */
    public boolean hasRole(ROLE role) {
        return sosMap.containsKey(role);
    }

    /**
     * Set the index to be used by this node.
     * @param index
     */
    public static void setIndex(Index index) {
        if (LocalSOSNode.index == null) {
            LocalSOSNode.index = index;
        }
    }

    public Storage getStorage() {
        return storage;
    }

    /**************************************************************************/
    /* PRIVATE METHODS */
    /**************************************************************************/

    private static void checkRequisites() throws SOSException {
        if (index == null) {
            throw new SOSException("Index not set");
        }
    }

    private static void initManifestManager() {
        manifestsManager = new ManifestsManager(storage, index);
    }

    private static void initNodeManager() throws SOSException {
        try {
            nodeManager = new NodeManager();
        } catch (NodeManagerException e) {
            throw new SOSException(e);
        }
    }


    private static void initIdentity() throws SOSException {
        try {
            identity = new IdentityImpl(configuration);
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new SOSException(e);
        }

    }

    private static void initSOSInstances() {
        sosMap = new HashMap<>();

        // TODO read configuration for roles
        sosMap.put(ROLE.CLIENT, new SOSClient(configuration, storage, manifestsManager, identity));
        sosMap.put(ROLE.STORAGE, new SOSStorage(configuration, storage, manifestsManager, identity));
        sosMap.put(ROLE.COORDINATOR, new SOSCoordinator(configuration, manifestsManager, identity, nodeManager));

        instance.setRoles((byte) (ROLE.CLIENT.mask | ROLE.COORDINATOR.mask | ROLE.STORAGE.mask));
    }

    private static void registerSOSProtocol() throws SOSProtocolException {
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(nodeManager);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            throw new SOSProtocolException(e);
        }
    }

    private static void backgroundProcesses() {
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

}

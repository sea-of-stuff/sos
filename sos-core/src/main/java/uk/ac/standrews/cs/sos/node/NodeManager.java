package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.node.SOS.SOSClient;
import uk.ac.standrews.cs.sos.node.SOS.SOSCoordinator;
import uk.ac.standrews.cs.sos.node.SOS.SOSStorage;
import uk.ac.standrews.cs.sos.node.internals.SQLiteDB;

import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the principal manager for this node.
 * The NodeManager should be used to access this SOS node and its information.
 * A node might have more than one role. A SOS instance is accessed by calling:
 * NodeManager.getInstance().getSOS(ROLE);
 *
 * This is a singleton class.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManager {

    private static Configuration configuration;
    private static Index index;
    private static ManifestsManager manifestsManager;

    private Node node;
    private Collection<Node> knownNodes; // TODO - there is not info about their available roles?
    private Identity identity;
    private HashMap<ROLE, SeaOfStuff> sosMap;

    private static NodeManager instance;

    private NodeManager() throws NodeManagerException {
        init();

        backgroundProcesses();
        registerSOSProtocol();
    }

    private void init() throws NodeManagerException {
        manifestsManager = new ManifestsManager(configuration, index);

        try {
            identity = new IdentityImpl(configuration);
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new NodeManagerException(e);
        }

        generateSOSNodeIfNone();
        registerSOSRoles();

        this.knownNodes = new HashSet<>();
        loadFromDB();
    }

    /**
     * Get the instance of the NodeManager.
     * The following must be set in order to get a valid instance:
     * - configuration
     * - index
     *
     * @return
     * @throws NodeManagerException
     */
    public static NodeManager getInstance() throws NodeManagerException {
        if (instance == null && configuration != null) {
            instance = new NodeManager();
        }

        return instance;
    }

    /**
     * Get all roles available for this node.
     *
     * @return
     */
    public ROLE[] getRoles() {
        return sosMap.keySet().toArray(new ROLE[sosMap.size()]);
    }

    /**
     * Get a SeaOfStuff instance for the given role.
     * @param role
     * @return
     */
    public SeaOfStuff getSOS(ROLE role) {
        return sosMap.get(role);
    }

    /**
     * Set the configuration to be used for this node.
     *
     * @param configuration
     * @return
     */
    public static boolean setConfiguration(Configuration configuration) {
        if (NodeManager.configuration == null) {
            NodeManager.configuration = configuration;
            return true;
        }
        return false;
    }

    /**
     * Set the index to be used for this node.
     *
     * @param index
     * @return
     */
    public static boolean setIndex(Index index) {
        if (NodeManager.index == null) {
            NodeManager.index = index;
            return true;
        }
        return false;
    }

    /**
     * Get the instance of this node.
     *
     * @return node of this instance of the NodeManager
     */
    public Node getThisNode() {
        return node;
    }

    /**
     * Add an arbitrary node to the manager.
     * This will be used to discovery nodes/data in the SOS.
     *
     * @param node
     */
    public void addNode(Node node) {
        knownNodes.add(node);
    }

    /**
     * Get all known nodes.
     *
     * @return
     */
    public Collection<Node> getKnownNodes() {
        return knownNodes;
    }

    /**
     * Get a SOS node given its guid identifier.
     *
     * @param guid
     * @return
     */
    public Node getNode(IGUID guid) {
        for(Node knownNode:knownNodes) {
            if (knownNode.getNodeGUID().equals(guid)) {
                return knownNode;
            }
        }
        return null;
    }

    /**
     * Persist the collection of known nodes.
     *
     * @throws DatabasePersistenceException
     */
    public void persistNodesTable() throws DatabasePersistenceException {
        try (Connection connection = SQLiteDB.getSQLiteConnection()) {
            boolean sqliteTableExists = SQLiteDB.checkSQLiteTableExists(connection);

            if (!sqliteTableExists) {
                SQLiteDB.createNodesTable(connection);
            }

            for (Node knownNode : knownNodes) {
                SQLiteDB.addNodeToTable(connection, knownNode);
            }

        } catch (SQLException e) {
            throw new DatabasePersistenceException(e);
        }
    }

    private void registerSOSRoles() {
        sosMap = new HashMap<>();

        // TODO read configuration for roles

        sosMap.put(ROLE.CLIENT, new SOSClient(configuration, manifestsManager, identity));
        sosMap.put(ROLE.STORAGE, new SOSStorage(configuration, manifestsManager, identity));
        sosMap.put(ROLE.COORDINATOR, new SOSCoordinator(configuration, manifestsManager, identity));
    }

    /**************************************************************************/
    /* PRIVATE METHODS */
    /**************************************************************************/

    private void registerSOSProtocol() {
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(this);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            System.err.println("NodeManager::registerSOSProtocol:" + e.getMessage());
        }
    }

    private void backgroundProcesses() {
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

    private void generateSOSNodeIfNone() throws NodeManagerException {
        try {
            Configuration configuration = Configuration.getInstance();
            node = configuration.getNode();
            if (node == null) {
                node = new SOSNode(GUIDFactory.generateRandomGUID());
                configuration.setNode(node);
            }
        } catch (ConfigurationException e) {
            throw new NodeManagerException();
        }
    }

    private void loadFromDB() throws NodeManagerException {
        try (Connection connection = SQLiteDB.getSQLiteConnection()) {
            boolean sqliteTableExists = SQLiteDB.checkSQLiteTableExists(connection);

            if (sqliteTableExists) {
                knownNodes.addAll(SQLiteDB.getNodes(connection));
            }

        } catch (SQLException | GUIDGenerationException | DatabasePersistenceException e) {
            throw new NodeManagerException(e);
        }
    }
}

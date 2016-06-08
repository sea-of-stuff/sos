package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SeaOfStuffException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.ROLE;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.node.SOS.SOSClient;
import uk.ac.standrews.cs.sos.node.internals.SQLiteDB;

import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Manager for this SOS node.
 * Singleton
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManager {

    private static SeaConfiguration configuration;
    private static Index index;
    private static ManifestsManager manifestsManager;

    private Node node;
    private Collection<Node> knownNodes;
    private Identity identity;
    private HashMap<ROLE, SeaOfStuff> sosMap;

    private static NodeManager instance;

    private NodeManager() throws NodeManagerException {
        try {
            init();
        } catch (SeaOfStuffException e) {
            throw new NodeManagerException();
        }

        generateSOSNodeIfNone();

        this.knownNodes = new HashSet<>();
        try {
            loadFromDB();
        } catch (DatabasePersistenceException e) {
            throw new NodeManagerException();
        }

        backgroundProcesses();
        registerSOSProtocol();

        registerSOSRoles();
    }

    private void init() throws SeaOfStuffException {
        manifestsManager = new ManifestsManager(configuration, index);

        try {
            identity = new IdentityImpl(configuration);
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new SeaOfStuffException(e);
        }

    }

    public static NodeManager getInstance() throws NodeManagerException {
        if (instance == null) {
            instance = new NodeManager();
        }

        return instance;
    }

    public ROLE[] getRoles() {
        return sosMap.keySet().toArray(new ROLE[sosMap.size()]);
    }

    public SeaOfStuff getSOS(ROLE role) {
        return sosMap.get(role);
    }

    public static boolean setConfiguration(SeaConfiguration configuration) {
        if (NodeManager.configuration == null) {
            NodeManager.configuration = configuration;
            return true;
        }
        return false;
    }

    public static boolean setIndex(Index index) {
        if (NodeManager.index == null) {
            NodeManager.index = index;
            return true;
        }
        return false;
    }

    /**
     *
     * @return node of this instance of the NodeManager
     */
    public Node getThisNode() {
        return node;
    }

    public void addNode(Node node) {
        knownNodes.add(node);
    }

    public Collection<Node> getKnownNodes() {
        return knownNodes;
    }

    public Node getNode(IGUID guid) {
        for(Node knownNode:knownNodes) {
            if (knownNode.getNodeGUID().equals(guid)) {
                return knownNode;
            }
        }

        return null;
    }

    public void persist() throws DatabasePersistenceException {
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

    // TODO - the behaviour of this method depends on the configuration
    // NOTE - also I am not sure if it is possible to have concurrent SOS implementations!
    private void registerSOSRoles() {
        sosMap = new HashMap<>();
        sosMap.put(ROLE.CLIENT, new SOSClient(configuration, manifestsManager, identity));
    }

    private void registerSOSProtocol() {
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(this);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            System.err.println("SeaOfStuffImpl::registerSOSProtocol:" + e.getMessage());
        }
    }

    private void backgroundProcesses() {
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

    private void generateSOSNodeIfNone() throws NodeManagerException {
        try {
            SeaConfiguration configuration = SeaConfiguration.getInstance();
            node = configuration.getNode();
            if (node == null) {
                node = new SOSNode(GUIDFactory.generateRandomGUID());
                configuration.setNode(node);
            }
        } catch (SeaConfigurationException e) {
            throw new NodeManagerException();
        }
    }

    private void loadFromDB() throws DatabasePersistenceException {
        try (Connection connection = SQLiteDB.getSQLiteConnection()) {
            boolean sqliteTableExists = SQLiteDB.checkSQLiteTableExists(connection);

            if (sqliteTableExists) {
                knownNodes.addAll(SQLiteDB.getNodes(connection));
            }

        } catch (SQLException | GUIDGenerationException e) {
            throw new DatabasePersistenceException(e);
        }
    }
}

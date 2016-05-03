package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManager {

    private Node node;
    private Collection<Node> knownNodes;

    public NodeManager() throws NodeManagerException {
        generateSOSNodeIfNone();

        this.knownNodes = new HashSet<>();
    }

    /**
     *
     * @return node of this instance of the SOS
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

    public void loadFromDB() throws DatabasePersistenceException {
        try (Connection connection = SQLiteDB.getSQLiteConnection()) {
            boolean sqliteTableExists = SQLiteDB.checkSQLiteTableExists(connection);

            if (sqliteTableExists) {
                knownNodes.addAll(SQLiteDB.getNodes(connection));
            }

        } catch (SQLException | GUIDGenerationException e) {
            throw new DatabasePersistenceException(e);
        }
    }

    public void loadFromDB(String dbPath) {
        // TODO - load from path
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

}

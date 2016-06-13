package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.node.internals.SQLiteDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

/**
 * This is the principal manager for this node.
 * The NodeManager should be used to access this SOSManager node and its information.
 * A node might have more than one role. A SOSManager instance is accessed by calling:
 * NodeManager.getInstance().getSOS(ROLE);
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManager {

    private Collection<Node> knownNodes; // TODO - there is not info about their available roles?

    public NodeManager() throws NodeManagerException {
        this.knownNodes = new HashSet<>();

        loadNodesFromDB();
    }

    /**
     * Add an arbitrary node to the manager.
     * This will be used to discovery nodes/data in the SOSManager.
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
     * Get a SOSManager node given its guid identifier.
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

    private void loadNodesFromDB() throws NodeManagerException {
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

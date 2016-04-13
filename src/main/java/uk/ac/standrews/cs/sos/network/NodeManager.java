package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistanceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManager {

    private Collection<Node> knownNodes;

    public NodeManager() {
        this.knownNodes = new HashSet<>();
    }

    public void addNode(Node node) {
        knownNodes.add(node);
    }

    public Collection<Node> getKnownNodes() {
        return knownNodes;
    }

    public void loadFromDB() throws DatabasePersistanceException {
        try (Connection connection = SQLiteDB.getSQLiteConnection()) {
            boolean sqliteTableExists = SQLiteDB.checkSQLiteTableExists(connection);

            if (sqliteTableExists) {
                knownNodes.addAll(SQLiteDB.getNodes(connection));
            }

        } catch (SQLException | GUIDGenerationException e) {
            throw new DatabasePersistanceException(e);
        }
    }

    public void loadFromDB(String dbPath) {
        // TODO - load from path
    }

    public void persist() throws DatabasePersistanceException {
        try (Connection connection = SQLiteDB.getSQLiteConnection()) {
            boolean sqliteTableExists = SQLiteDB.checkSQLiteTableExists(connection);

            if (!sqliteTableExists) {
                SQLiteDB.createNodesTable(connection);
            }

            for (Node knownNode : knownNodes) {
                SQLiteDB.addNodeToTable(connection, knownNode);
            }

        } catch (SQLException e) {
            throw new DatabasePersistanceException(e);
        }
    }

}

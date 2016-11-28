package uk.ac.standrews.cs.sos.node.directory.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.node.SOSNode;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.node.directory.database.DatabaseTypes.MYSQL_DB;
import static uk.ac.standrews.cs.sos.node.directory.database.DatabaseTypes.SQLITE_DB;

/**
 * SQLite database used to store information about:
 * - configuration for this node
 * - known nodes for this LocalSOSNode node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SQLDatabase implements NodesDatabase {

    private DatabaseType databaseType;
    private String pathname;

    public SQLDatabase(DatabaseType databaseType, String pathname) throws DatabaseException {
        this.databaseType = databaseType;
        this.pathname = pathname;
    }

    @Override
    public void addNode(Node node) throws DatabaseConnectionException {

        ConnectionSource connection = null;
        try {
            connection = getDBConnection();
            createNodesTable(connection);

            Dao<SOSNode, String> nodesDAO = DaoManager.createDao(connection, SOSNode.class);

            SOSNode clone = new SOSNode(node);
            nodesDAO.createOrUpdate(clone);
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseConnectionException(e);
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public Set<SOSNode> getNodes() throws DatabaseConnectionException {

        ConnectionSource connection = null;
        Set<SOSNode> nodes;
        try {
            connection = getDBConnection();
            createNodesTable(connection);

            Dao<SOSNode, String> nodesDAO = DaoManager.createDao(connection, SOSNode.class);
            nodes = new HashSet<>(nodesDAO.queryForAll());
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseConnectionException(e);
        } finally {
            closeConnection(connection);
        }

        return nodes;
    }

    private void createNodesTable(ConnectionSource connection) throws SQLException {
        TableUtils.createTableIfNotExists(connection, SOSNode.class);
    }

    private ConnectionSource getDBConnection() throws DatabaseException {

        if (databaseType.equals(SQLITE_DB)) {
            return getSQLiteConnection();
        } else if (databaseType.equals(MYSQL_DB)) {
            return getMySQLConnection();
        }

        throw new DatabaseException("Unable to recognise the type of database "
                + databaseType.toString() + " in the configuration properties");
    }

    private ConnectionSource getSQLiteConnection() throws DatabaseException {
        ConnectionSource connection;
        try {
            String databaseUrl = "jdbc:sqlite:" + pathname;
            connection = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to get DB connection", e);
        }

        return connection;
    }

    private ConnectionSource getMySQLConnection() {
        throw new UnsupportedOperationException();
    }

    private void closeConnection(ConnectionSource connection) throws DatabaseConnectionException {

        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }

    }

}

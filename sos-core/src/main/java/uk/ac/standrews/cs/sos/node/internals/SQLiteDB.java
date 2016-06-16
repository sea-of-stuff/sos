package uk.ac.standrews.cs.sos.node.internals;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.node.SOSNode;

import java.sql.SQLException;
import java.util.Collection;

/**
 * SQLite database used to store information about known nodes for this LocalSOSNode node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SQLiteDB {

    public static void createNodesTable(ConnectionSource connection) throws SQLException {
        TableUtils.createTableIfNotExists(connection, SOSNode.class);
    }

    public static void addNodeToTable(ConnectionSource connection, Node node) throws SQLException {
        Dao<SOSNode,String> nodesDAO = DaoManager.createDao(connection, SOSNode.class);
        nodesDAO.create((SOSNode) node);
    }

    public static Collection<SOSNode> getNodes(ConnectionSource connection) throws SQLException, GUIDGenerationException {
        Dao<SOSNode,String> nodesDAO = DaoManager.createDao(connection, SOSNode.class);
        return nodesDAO.queryForAll();
    }

    public static ConnectionSource getSQLiteConnection() throws DatabasePersistenceException {
        ConnectionSource connection;
        try {
            SOSFile dbDump = Configuration.getInstance().getDatabaseDump();
            String databaseUrl = "jdbc:sqlite:" + dbDump.getPathname();
            connection = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException | ConfigurationException e) {
            throw new DatabasePersistenceException(e.getClass().getName() + ": " + e.getMessage());
        }

        return connection;
    }
}

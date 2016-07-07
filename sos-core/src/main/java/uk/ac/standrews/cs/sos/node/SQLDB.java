package uk.ac.standrews.cs.sos.node;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodeDatabase;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * SQLite database used to store information about:
 * - configuration for this node
 * - known nodes for this LocalSOSNode node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SQLDB implements NodeDatabase {

    public static ConnectionSource getSQLConnection() throws DatabasePersistenceException {

        ConnectionSource connection;
        switch(Config.db_type) {
            case Config.DB_TYPE_SQLITE:
                connection = getSQLiteConnection();
                break;
            case Config.DB_TYPE_MYSQL:
                connection = getMySQLConnection();
                break;
            default:
                throw new DatabasePersistenceException("Unable to recognise the type of database in the configuration properties");
        }

        return connection;
    }

    public static Config getConfiguration(ConnectionSource connection) throws SQLException {
        Dao<Config, String> confiDAO = DaoManager.createDao(connection, Config.class);
        Iterator<Config> iterator = confiDAO.queryForAll().iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
          throw new SQLException("No configuration row in DB");
        }
    }

    public static void createNodesTable(ConnectionSource connection) throws SQLException {
        TableUtils.createTableIfNotExists(connection, SOSNode.class);
    }

    public static void addNodeToTable(ConnectionSource connection, Node node) throws SQLException {
        Dao<SOSNode, String> nodesDAO = DaoManager.createDao(connection, SOSNode.class);
        nodesDAO.create((SOSNode) node);
    }

    public static Collection<SOSNode> getNodes(ConnectionSource connection) throws SQLException, GUIDGenerationException {
        Dao<SOSNode, String> nodesDAO = DaoManager.createDao(connection, SOSNode.class);
        return nodesDAO.queryForAll();
    }

    private static ConnectionSource getSQLiteConnection() throws DatabasePersistenceException {
        ConnectionSource connection;
        try {
            String databaseUrl = "jdbc:sqlite:" + Config.DB_DUMP_FILE.getPathname();
            connection = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException e) {
            throw new DatabasePersistenceException(e.getClass().getName() + ": " + e.getMessage());
        }

        return connection;
    }

    private static ConnectionSource getMySQLConnection() {
        throw new UnsupportedOperationException();
    }

}

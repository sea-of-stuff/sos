package uk.ac.standrews.cs.sos.node.internals;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.node.SOSNode;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SQLiteDB {

    private final static String SQL_CHECK_ANY_TABLE_EXISTS = "SELECT name FROM sqlite_master WHERE type=\'table\' and name=\'nodes\'";
    private final static String SQL_CREATE_NODES_TABLE = "CREATE TABLE nodes " +
            "(nodeid    TEXT PRIMARY KEY    NOT NULL, " +
            " hostname  TEXT                NOT NULL, " +
            " port      INT                 NOT NULL, " +
            " nodetype  INT                 NOT NULL)";

    // http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace/4330694#4330694
    private final static String SQL_ADD_NODE = "INSERT OR REPLACE INTO nodes " +
            "(nodeid, hostname, port, nodetype) " +
            "VALUES (?, ?, ?, ?)";
    private final static String SQL_GET_NODES = "SELECT nodeid, hostname, port, " +
            "nodetype FROM nodes";

    public static boolean checkSQLiteTableExists(Connection connection) throws SQLException {

        boolean retval;
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_CHECK_ANY_TABLE_EXISTS);
             ResultSet resultSet  = preparedStatement.executeQuery()) {
            retval = resultSet.next();
        }

        return retval;
    }

    public static void createNodesTable(Connection connection) throws SQLException {

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_CREATE_NODES_TABLE)) {
            preparedStatement.executeUpdate();
        }
    }

    public static boolean addNodeToTable(Connection connection, Node node) throws SQLException {

        boolean retval;
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_ADD_NODE)) {

            preparedStatement.setString(1, node.getNodeGUID().toString());
            preparedStatement.setString(2, node.getHostAddress().getHostName());
            preparedStatement.setInt(3, node.getHostAddress().getPort());
            preparedStatement.setInt(4, 0b0); // FIXME - this should not be hardcoded

            retval = preparedStatement.execute();
        }

        return retval;
    }

    public static Collection<Node> getNodes(Connection connection) throws SQLException, GUIDGenerationException {

        Collection<Node> retval = new HashSet<>();
        try (PreparedStatement preparedStatement =
                    connection.prepareStatement(SQL_GET_NODES);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while(resultSet.next()) {
                IGUID guid = GUIDFactory.recreateGUID(resultSet.getString(1));
                String hostname = resultSet.getString(2);
                int port = resultSet.getInt(3);
                byte nodeRoles = resultSet.getByte(4);
                InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);

                Node node = new SOSNode(guid, inetSocketAddress);
                setRoles(node, nodeRoles);
                retval.add(node);
            }
        }
        return retval;
    }

    public static void setRoles(Node node, byte nodeRoles) {
        /*
        if ((nodeRoles & ROLE.CONSUMER_MASK) != 0) {
            node.setNodeRole(new Consumer());
        }

        if ((nodeRoles & ROLE.COORDINATOR_MASK) != 0) {
            node.setNodeRole(new Coordinator());
        }

        if ((nodeRoles & ROLE.PRODUCER_MASK) != 0) {
            node.setNodeRole(new Producer());
        }
        */

    }

    public static Connection getSQLiteConnection() throws DatabasePersistenceException {
        Connection connection;
        try {
            SOSFile dbDump = Configuration.getInstance().getDatabaseDump();

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" +
                    dbDump.getPathname());
        } catch (Exception e) {
            throw new DatabasePersistenceException(e.getClass().getName() + ": " + e.getMessage());
        }

        return connection;
    }
}

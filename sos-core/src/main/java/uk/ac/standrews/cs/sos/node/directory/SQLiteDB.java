package uk.ac.standrews.cs.sos.node.directory;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.utils.FileHelper;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SQLiteDB implements NodesDatabase {

    private final static String SQL_CHECK_ANY_TABLE_EXISTS = "SELECT name FROM sqlite_master WHERE type=\'table\' and name=\'nodes\'";
    private final static String SQL_CREATE_NODES_TABLE = "CREATE TABLE `nodes` " +
            "(`DB_nodeid`       VARCHAR , " +
            "`DB_hostname`      VARCHAR NOT NULL , " +
            "`DB_port`          INTEGER NOT NULL , " +
            "`DB_is_agent`      BOOLEAN NOT NULL , " +
            "`DB_is_storage`    BOOLEAN NOT NULL , " +
            "`DB_is_dds`        BOOLEAN NOT NULL , " +
            "`DB_is_nds`        BOOLEAN NOT NULL , " +
            "`DB_is_mms`        BOOLEAN NOT NULL , " +
            "PRIMARY KEY (`DB_nodeid`) )";


    // http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace/4330694#4330694
    private final static String SQL_ADD_NODE = "INSERT OR REPLACE INTO nodes " +
            "(DB_nodeid, DB_hostname, DB_port, DB_is_agent, DB_is_storage, DB_is_dds, DB_is_nds, DB_is_mms) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_GET_NODES = "SELECT DB_nodeid, DB_hostname, DB_port, " +
            "DB_is_agent, DB_is_storage, DB_is_dds, DB_is_nds, DB_is_mms FROM nodes";


    private String path;

    public SQLiteDB(String path) throws DatabaseException {
        this.path = path;

        FileHelper.MakePath(path);

        try (Connection connection = getSQLiteConnection()) {
            boolean tableExists = checkSQLiteTableExists(connection);
            if (!tableExists) {
                createNodesTable(connection);
            }

        } catch (SQLException | DatabaseConnectionException e) {
            throw new DatabaseException(e);
        }
    }

    private boolean checkSQLiteTableExists(Connection connection) throws SQLException {

        boolean retval;
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_CHECK_ANY_TABLE_EXISTS);
             ResultSet resultSet  = preparedStatement.executeQuery()) {
            retval = resultSet.next();
        }

        return retval;
    }

    private void createNodesTable(Connection connection) throws SQLException {

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_CREATE_NODES_TABLE)) {
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void addNode(Node node) throws DatabaseConnectionException {

        try (Connection connection = getSQLiteConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_ADD_NODE)) {

            preparedStatement.setString(1, node.getNodeGUID().toString());
            preparedStatement.setString(2, node.getHostAddress().getHostName());
            preparedStatement.setInt(3, node.getHostAddress().getPort());
            preparedStatement.setBoolean(4, node.isAgent());
            preparedStatement.setBoolean(5, node.isStorage());
            preparedStatement.setBoolean(6, node.isDDS());
            preparedStatement.setBoolean(7, node.isNDS());
            preparedStatement.setBoolean(8, node.isMMS());

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public Set<SOSNode> getNodes() throws DatabaseConnectionException {
        Set<SOSNode> retval = new HashSet<>();

        try (Connection connection = getSQLiteConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_NODES);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()) {
                IGUID guid = GUIDFactory.recreateGUID(resultSet.getString(1));
                String hostname = resultSet.getString(2);
                int port = resultSet.getInt(3);
                boolean isAgent = resultSet.getBoolean(4);
                boolean isStorage = resultSet.getBoolean(5);
                boolean isDDS = resultSet.getBoolean(6);
                boolean isNDS = resultSet.getBoolean(7);
                boolean isMCS = resultSet.getBoolean(8);

                SOSNode node = new SOSNode(guid, hostname, port, isAgent, isStorage, isDDS,isNDS, isMCS);

                retval.add(node);
            }
        } catch (SQLException | GUIDGenerationException e) {
            throw new DatabaseConnectionException(e);
        }

        return retval;
    }

    /**
     * Caller must make sure that the connection is closed
     * @return
     * @throws DatabaseConnectionException
     */
    private Connection getSQLiteConnection() throws DatabaseConnectionException {
        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            throw new DatabaseConnectionException(e);
        }

        return connection;
    }

}

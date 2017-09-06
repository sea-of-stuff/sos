package uk.ac.standrews.cs.sos.impl.database;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesDatabaseImpl extends AbstractDatabase implements NodesDatabase {

    // TODO - rename DB_fields!!
    private final static String SQL_CHECK_NODES_TABLE_EXISTS = "SELECT name FROM sqlite_master WHERE type=\'table\' and name=\'nodes\'";
    private final static String SQL_CREATE_NODES_TABLE = "CREATE TABLE `nodes` " +
            "(`DB_nodeid`       VARCHAR , " +
            "`cert`             VARCHAR NOT NULL , " +
            "`DB_hostname`      VARCHAR NOT NULL , " +
            "`DB_port`          INTEGER NOT NULL , " +
            "`DB_is_agent`      BOOLEAN NOT NULL , " +
            "`DB_is_storage`    BOOLEAN NOT NULL , " +
            "`DB_is_dds`        BOOLEAN NOT NULL , " +
            "`DB_is_nds`        BOOLEAN NOT NULL , " +
            "`DB_is_mms`        BOOLEAN NOT NULL , " +
            "`DB_is_cms`        BOOLEAN NOT NULL , " +
            "`DB_is_rms`        BOOLEAN NOT NULL , " +
            "PRIMARY KEY (`DB_nodeid`) )";

    // http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace/4330694#4330694
    private final static String SQL_ADD_NODE = "INSERT OR REPLACE INTO nodes " +
            "(DB_nodeid, cert, DB_hostname, DB_port, DB_is_agent, DB_is_storage, DB_is_dds, DB_is_nds, DB_is_mms, DB_is_cms, DB_is_rms) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_GET_NODES = "SELECT DB_nodeid, cert, DB_hostname, DB_port, " +
            "DB_is_agent, DB_is_storage, DB_is_dds, DB_is_nds, DB_is_mms, DB_is_cms, DB_is_rms FROM nodes";


    public NodesDatabaseImpl(String path) throws DatabaseException {
        super(path);

        try (Connection connection = getSQLiteConnection()) {

            boolean tableExists = executeQuery(connection, SQL_CHECK_NODES_TABLE_EXISTS);
            if (!tableExists) {
                executeUpdate(connection, SQL_CREATE_NODES_TABLE);
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void addNode(Node node) throws DatabaseConnectionException {

        try (Connection connection = getSQLiteConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ADD_NODE)) {

            preparedStatement.setString(1, node.getNodeGUID().toMultiHash());
            preparedStatement.setString(2, DigitalSignature.getCertificateString(node.getSignatureCertificate()));
            preparedStatement.setString(3, node.getHostAddress().getHostName());
            preparedStatement.setInt(4, node.getHostAddress().getPort());
            preparedStatement.setBoolean(5, node.isAgent());
            preparedStatement.setBoolean(6, node.isStorage());
            preparedStatement.setBoolean(7, node.isDDS());
            preparedStatement.setBoolean(8, node.isNDS());
            preparedStatement.setBoolean(9, node.isMMS());
            preparedStatement.setBoolean(10, node.isCMS());
            preparedStatement.setBoolean(11, node.isRMS());

            preparedStatement.execute();

        } catch (SQLException | CryptoException e) {
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
                PublicKey cert = DigitalSignature.getCertificate(resultSet.getString(2));
                String hostname = resultSet.getString(3);
                int port = resultSet.getInt(4);
                boolean isAgent = resultSet.getBoolean(5);
                boolean isStorage = resultSet.getBoolean(6);
                boolean isDDS = resultSet.getBoolean(7);
                boolean isNDS = resultSet.getBoolean(8);
                boolean isMMS = resultSet.getBoolean(9);
                boolean isCMS = resultSet.getBoolean(10);
                boolean isRMS = resultSet.getBoolean(11);

                SOSNode node = new SOSNode(guid, cert, hostname, port, isAgent, isStorage, isDDS,isNDS, isMMS, isCMS, isRMS);

                retval.add(node);
            }
        } catch (SQLException | GUIDGenerationException | CryptoException e) {
            throw new DatabaseConnectionException(e);
        }

        return retval;
    }


}

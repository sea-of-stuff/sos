package uk.ac.standrews.cs.sos.impl.database;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextVersionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextsDatabase extends AbstractDatabase {

    private final static String SQL_CHECK_CONTEXTS_TABLE_EXISTS = "SELECT name FROM sqlite_master WHERE type=\'table\' and name=\'contexts\'";
    private final static String SQL_CREATE_CONTEXTS_TABLE = "CREATE TABLE `contexts` " +
            "(`context_id`        VARCHAR , " +
            "`version_id`         VARCHAR , " +
            "`pred_result`        BOOLEAN NOT NULL , " +
            "`timestamp`          INTEGER NOT NULL , " + // will be a long
            "`policy_satisfied`   BOOLEAN NOT NULL , " +
            "`evict`              BOOLEAN DEFAULT 0 , " + // FALSE
            "PRIMARY KEY (`context_id`, `version_id`) )";


    private final static String SQL_ADD_ENTRY = "INSERT OR REPLACE INTO contexts " +
            "(context_id, version_id, pred_result, timestamp, policy_satisfied) " +
            "VALUES (?, ?, ?, ?, ?)";

    private final static String SQL_GET_ENTRIES = "SELECT version_id, pred_result, timestamp, policy_satisfied " +
            "FROM contexts WHERE evict == 0";

    private final static String SQL_GET_ENTRY = "SELECT pred_result, timestamp, policy_satisfied " +
            "FROM contexts WHERE context_id=? and version_id=?";

    private final static String SQL_EVICT_ENTRY = "UPDATE contexts SET evict=1 WHERE context_id=? version_id=?";

    public ContextsDatabase(String path) throws DatabaseException {
        super(path);

        try (Connection connection = getSQLiteConnection()) {

            boolean tableExists = executeQuery(connection, SQL_CHECK_CONTEXTS_TABLE_EXISTS);
            if (!tableExists) {
                executeUpdate(connection, SQL_CREATE_CONTEXTS_TABLE);
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void addEntry(IGUID context, IGUID version, ContextVersionInfo contextVersionInfo) {

        try (Connection connection = getSQLiteConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ADD_ENTRY)) {

            preparedStatement.setString(1, context.toMultiHash());
            preparedStatement.setString(2, version.toMultiHash());
            preparedStatement.setBoolean(3, contextVersionInfo.predicateResult);
            preparedStatement.setLong(4, contextVersionInfo.timestamp);
            preparedStatement.setBoolean(5, contextVersionInfo.policySatisfied);

            preparedStatement.execute();

        } catch (SQLException | DatabaseConnectionException ignored) { }
    }

    public void evict(IGUID context, IGUID version) {

        try (Connection connection = getSQLiteConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_EVICT_ENTRY)) {

            preparedStatement.setString(1, context.toMultiHash());
            preparedStatement.setString(2, version.toMultiHash());

            preparedStatement.executeUpdate();

        } catch (SQLException | DatabaseConnectionException ignored) { }
    }

    public ContextVersionInfo getEntry(IGUID context, IGUID version) {

        try (Connection connection = getSQLiteConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_ENTRY)) {

            preparedStatement.setString(1, context.toMultiHash());
            preparedStatement.setString(2, version.toMultiHash());


            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                ContextVersionInfo contextVersionInfo = new ContextVersionInfo();
                contextVersionInfo.predicateResult = resultSet.getBoolean(1);
                contextVersionInfo.timestamp = resultSet.getLong(2);
                contextVersionInfo.policySatisfied = resultSet.getBoolean(3);

                return contextVersionInfo;

            }

        } catch (SQLException | DatabaseConnectionException ignored) { }

        return new ContextVersionInfo();
    }

    public boolean entryExists(IGUID context, IGUID version) {

        try (Connection connection = getSQLiteConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_ENTRY)) {

            preparedStatement.setString(1, context.toMultiHash());
            preparedStatement.setString(2, version.toMultiHash());

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException | DatabaseConnectionException e) {

            return false;
        }

    }

    public Set<IGUID> getVersionsThatPassedPredicateTest(IGUID context) {

        Set<IGUID> contents = new LinkedHashSet<>();

        try (Connection connection = getSQLiteConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_ENTRIES)) {

            preparedStatement.setString(1, context.toMultiHash());

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                try {
                    contents.add(GUIDFactory.recreateGUID(resultSet.getString(1)));
                } catch (GUIDGenerationException ignored) { /* SKIP - DO NOTHING */ }
            }

        } catch (SQLException | DatabaseConnectionException ignored) { }

        return contents;

    }

    public Map<IGUID, ContextVersionInfo> getContentsThatPassedPredicateTestRows(IGUID context) {

        HashMap<IGUID, ContextVersionInfo> contents = new LinkedHashMap<>();

        try (Connection connection = getSQLiteConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_ENTRIES)) {

            preparedStatement.setString(1, context.toMultiHash());

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {

                try {
                    IGUID version = GUIDFactory.recreateGUID(resultSet.getString(1));
                    ContextVersionInfo contextVersionInfo = new ContextVersionInfo();
                    contextVersionInfo.predicateResult = resultSet.getBoolean(2);
                    contextVersionInfo.timestamp = resultSet.getLong(3);
                    contextVersionInfo.policySatisfied = resultSet.getBoolean(4);

                    contents.put(version, contextVersionInfo);

                } catch (GUIDGenerationException ignored) { /* SKIP - DO NOTHING */ }

            }

            return contents;

        } catch (SQLException | DatabaseConnectionException ignored) { }

        return contents;

    }

    public void getVersionsWithPolicyNotSatisfied(IGUID context) {

    }


}

package uk.ac.standrews.cs.sos.impl.node.directory;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * The CP class is a Connection Pool for the local DB
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CP {

    /**
     * This is the path for the DB instance and must be set before creating the instance.
     */
    public static String path;

    private static final HikariDataSource ds = new HikariDataSource();

    private static CP instance;
    public static CP instance() throws SQLException {
        if (instance == null) {
            instance = new CP();
        }
        return instance;
    }

    private CP() throws SQLException {

        if (path == null || path.isEmpty()) throw new SQLException("DB Path not set");

        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setJdbcUrl("jdbc:sqlite:" + path);

        ds.addDataSourceProperty("cachePrepStmts", true);
        ds.addDataSourceProperty("prepStmtCacheSize", 250);
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        ds.addDataSourceProperty("useServerPrepStmts", true);
        ds.addDataSourceProperty("autoReconnect", true);
        ds.setLeakDetectionThreshold(15000);
        ds.setMaxLifetime(MINUTES.toMillis(5)); // Default is 10
        ds.setMaximumPoolSize(3); // Default is 10, but 3 should be enough
    }

    public void kill() {
        ds.close();
    }

    /**
     * Get a connection from this connection pool
     *
     * @return
     * @throws SQLException
     */
    public Connection connection() throws SQLException {
        return ds.getConnection();
    }
}

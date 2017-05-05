package uk.ac.standrews.cs.sos.impl.node.directory;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CP {

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

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

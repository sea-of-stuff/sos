package uk.ac.standrews.cs.sos.node.database;

import com.j256.ormlite.support.ConnectionSource;
import uk.ac.standrews.cs.sos.interfaces.node.DBConnection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SQLConnection implements DBConnection {

    ConnectionSource connection;

    public SQLConnection(ConnectionSource connection) {
        this.connection = connection;
    }

    public ConnectionSource getConnection() {
        return connection;
    }
}

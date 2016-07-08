package uk.ac.standrews.cs.sos.interfaces.node;

import com.j256.ormlite.support.ConnectionSource;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DBConnection {

    ConnectionSource getConnectionSource();

    void close() throws DatabaseConnectionException;
}

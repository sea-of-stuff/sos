package uk.ac.standrews.cs.sos;

import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Utilities {

    public static Config createdDummyConfig() throws IOException, StorageException {
        Config.db_type = Config.DB_TYPE_SQLITE;
        Config.initDatabaseInfo();

        return new Config();
    }
}

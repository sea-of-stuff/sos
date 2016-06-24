package uk.ac.standrews.cs.sos.node;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ConfigTest {

    @Test
    public void initDefaultDBTest() {
        assertEquals(Config.db_type, Config.DB_TYPE_SQLITE);
        assertNull(Config.DB_DUMP_FILE);

        Config.initDatabase();

        assertEquals(Config.db_type, Config.DB_TYPE_SQLITE);
        assertNotNull(Config.DB_DUMP_FILE);
    }

    @Test
    public void initCustomDBTest() {
        Config.db_path = "custom";
        Config.initDatabase();

        assertEquals(Config.db_type, Config.DB_TYPE_SQLITE);
        assertNotNull(Config.DB_DUMP_FILE);
        assertEquals(Config.DB_DUMP_FILE.getParent().getName(), "custom");
    }

    @Test
    public void dbAuthNullByDefaultTest() {
        assertNull(Config.db_hostname);
        assertNull(Config.db_username);
        assertNull(Config.db_password);
    }

    @Test
    public void DBDumpFileChangesOnInitialisationTest() {
        Config.initDatabase();

        assertEquals(Config.db_type, Config.DB_TYPE_SQLITE);
        assertNotNull(Config.DB_DUMP_FILE);
        SOSFile actual = Config.DB_DUMP_FILE;

        // Update path, but do not initialise
        Config.db_path = "custom";
        assertEquals(actual, Config.DB_DUMP_FILE);

        // Initialise and verify that dump file has changed
        Config.initDatabase();
        assertNotEquals(actual, Config.DB_DUMP_FILE);
    }
}
package uk.ac.standrews.cs.sos.node;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ConfigTest {


    @Test
    public void initDefaultDBTest() {
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
}
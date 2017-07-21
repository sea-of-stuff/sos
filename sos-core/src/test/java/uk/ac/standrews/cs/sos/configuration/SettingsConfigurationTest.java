package uk.ac.standrews.cs.sos.configuration;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;

import java.io.File;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.SetUpTest.TEST_RESOURCES_PATH;
import static uk.ac.standrews.cs.sos.configuration.SettingsConfiguration.HOME_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SettingsConfigurationTest {

    private File configFile;

    @BeforeMethod
    public void setUp() {
        configFile = new File(TEST_RESOURCES_PATH + "config.json");
    }

    @Test
    public void constructorTest() throws ConfigurationException {

        new SettingsConfiguration(configFile); // No exception is thrown
    }

    @Test
    public void guidTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        IGUID guid = settings.getSettingsObj().getNodeGUID();
        assertFalse(guid.isInvalid());
        assertEquals(guid.toString(), "6b67f67f31908dd0e574699f163eda2cc117f7f4");
    }

    @Test
    public void rolesTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.RolesModel roles = settings.getSettingsObj().getRoles();
        assertTrue(roles.isAgent());
        assertTrue(roles.isStorage());
        assertFalse(roles.isDDS());
        assertFalse(roles.isNDS());
        assertFalse(roles.isMMS());
        assertFalse(roles.isCMS());
        assertTrue(roles.isRMS());
    }

    @Test
    public void dbTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.DatabaseSettings databaseSettings = settings.getSettingsObj().getDatabase();
        assertEquals(databaseSettings.getFilename(), "dump.db");
    }

    @Test
    public void restTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.RESTSettings restSettings = settings.getSettingsObj().getRest();
        assertEquals(restSettings.getPort(), 8080);
    }

    @Test
    public void webDAVTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.WebDAVSettings webDAVSettings = settings.getSettingsObj().getWebDAV();
        assertEquals(webDAVSettings.getPort(), 8081);
    }


    @Test
    public void webAPPTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.WebAPPSettings webAPPSettings = settings.getSettingsObj().getWebAPP();
        assertEquals(webAPPSettings.getPort(), 8082);
    }

    @Test
    public void keysTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.KeysSettings keysSettings = settings.getSettingsObj().getKeys();
        assertEquals(keysSettings.getLocation(), HOME_PATH + "/sos/keys/");
    }

    @Test
    public void storageTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.StorageSettings storageSettings = settings.getSettingsObj().getStorage();
        assertEquals(storageSettings.getType(), "local");
        assertEquals(storageSettings.getLocation(), HOME_PATH + "/sos/");
    }

}
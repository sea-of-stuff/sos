package uk.ac.standrews.cs.sos;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;

import java.io.File;
import java.util.List;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.SettingsConfiguration.HOME_PATH;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SettingsConfigurationTest {

    private File configFile;

    @BeforeMethod
    public void setUp() {
        configFile = new File(TEST_CONFIGURATIONS_PATH + "config_test.json");
    }

    @Test
    public void constructorTest() throws ConfigurationException {

        new SettingsConfiguration(configFile); // No exception is thrown
    }

    @Test
    public void guidTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        // NOTE: The node GUID is not set from the configuration file, but it is generated using the node certificate.
        IGUID guid = settings.getSettingsObj().guid();
        assertTrue(guid.isInvalid());
    }

    @Test
    public void servicesTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings services = settings.getSettingsObj().getServices();
        assertTrue(services.getStorage().isExposed());
        assertFalse(services.getMds().isExposed());
        assertFalse(services.getNds().isExposed());
        assertFalse(services.getMms().isExposed());
        assertFalse(services.getCms().isExposed());
        assertTrue(services.getRms().isExposed());
    }

    @Test
    public void dbTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.DatabaseSettings databaseSettings = settings.getSettingsObj().getDatabase();
        assertEquals(databaseSettings.getFilename(), "node.db");
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

        SettingsConfiguration.Settings.StoreSettings storeSettings = settings.getSettingsObj().getStore();
        assertEquals(storeSettings.getType(), "local");
        assertEquals(storeSettings.getLocation(), HOME_PATH + "/sos/");

        assertNotNull(storeSettings.getCastoreBuilder());
        CastoreBuilder castoreBuilder = storeSettings.getCastoreBuilder();
        assertEquals(castoreBuilder.getType(), CastoreType.LOCAL);
        assertEquals(castoreBuilder.getRoot(), HOME_PATH + "/sos/");
    }

    @Test
    public void notEmptyBoostrapNodes() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        List<SettingsConfiguration.Settings.SimpleNode> bootstrap = settings.getSettingsObj().getBootstrapNodes();
        assertFalse(bootstrap.isEmpty());
    }

    @Test
    public void cmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.CMSSettings cmsSettings = settings.getSettingsObj().getServices().getCms();
        assertFalse(cmsSettings.isExposed());
        assertTrue(cmsSettings.isAutomatic());

        testThreadSettings(cmsSettings.getPredicateThread(), 30, 60);
        testThreadSettings(cmsSettings.getPoliciesThread(), 45, 60);
        testThreadSettings(cmsSettings.getCheckPoliciesThread(), 45, 60);
        testThreadSettings(cmsSettings.getGetdataThread(), 60, 60);
        testThreadSettings(cmsSettings.getSpawnThread(), 90, 120);
    }

    @Test
    public void mdsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.MDSSettings mdsSettings = settings.getSettingsObj().getServices().getMds();
        assertFalse(mdsSettings.isExposed());
    }

    @Test
    public void rmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.RMSSettings rmsSettings = settings.getSettingsObj().getServices().getRms();
        assertTrue(rmsSettings.isExposed());
    }

    @Test
    public void ndsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.NDSSettings ndsSettings = settings.getSettingsObj().getServices().getNds();
        assertFalse(ndsSettings.isExposed());
    }

    @Test
    public void mmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.MMSSettings mmsSettings = settings.getSettingsObj().getServices().getMms();
        assertFalse(mmsSettings.isExposed());
    }

    private void testThreadSettings(SettingsConfiguration.Settings.ThreadSettings threadSettings, int initDelay, int period) {

        assertEquals(threadSettings.getInitialDelay(), initDelay);
        assertEquals(threadSettings.getPeriod(), period);
    }

}

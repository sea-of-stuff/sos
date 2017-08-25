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

        IGUID guid = settings.getSettingsObj().getNodeGUID();
        assertFalse(guid.isInvalid());
        assertEquals(guid.toString(), "0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4");
        assertEquals(guid.toMultiHash(), "SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4");
    }

    @Test
    public void servicesTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings services = settings.getSettingsObj().getServices();
        assertTrue(services.getStorage().isExposed());
        assertFalse(services.getDds().isExposed());
        assertFalse(services.getNds().isExposed());
        assertFalse(services.getMms().isExposed());
        assertFalse(services.getCms().isExposed());
        assertTrue(services.getRms().isExposed());
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

        List<SettingsConfiguration.Settings.NodeSettings> bootstrap = settings.getSettingsObj().getBootstrapNodes();
        assertFalse(bootstrap.isEmpty());
    }

    @Test
    public void cmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.CMSSettings cmsSettings = settings.getSettingsObj().getServices().getCms();
        assertFalse(cmsSettings.isExposed());
        assertEquals(cmsSettings.getIndexFile(), "cms.index");
        assertTrue(cmsSettings.isAutomatic());

        testThreadSettings(cmsSettings.getPredicateThread(), 30, 60);
        testThreadSettings(cmsSettings.getPoliciesThread(), 45, 60);
        testThreadSettings(cmsSettings.getCheckPoliciesThread(), 45, 60);
        testThreadSettings(cmsSettings.getGetdataThread(), 60, 60);
        testThreadSettings(cmsSettings.getSpawnThread(), 90, 120);
    }

    @Test
    public void ddsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.DDSSettings ddsSettings = settings.getSettingsObj().getServices().getDds();
        assertFalse(ddsSettings.isExposed());
        assertEquals(ddsSettings.getCacheFile(), "manifests.cache");
        assertEquals(ddsSettings.getIndexFile(), "dds.index");
    }

    @Test
    public void rmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvanceServicesSettings.RMSSettings rmsSettings = settings.getSettingsObj().getServices().getRms();
        assertTrue(rmsSettings.isExposed());
        assertEquals(rmsSettings.getCacheFile(), "usro.cache");
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
package uk.ac.standrews.cs.sos;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;

import java.io.File;
import java.util.List;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.SettingsConfiguration.HOME_PATH;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

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

        SettingsConfiguration.Settings.AdvancedRolesSettings roles = settings.getSettingsObj().getRoles();
        assertTrue(roles.getStorage().isExposed());
        assertFalse(roles.getDds().isExposed());
        assertFalse(roles.getNds().isExposed());
        assertFalse(roles.getMms().isExposed());
        assertFalse(roles.getCms().isExposed());
        assertTrue(roles.getRms().isExposed());
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
    public void emptyBoostrapNodes() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        List<SettingsConfiguration.Settings.NodeSettings> bootstrap = settings.getSettingsObj().getBootstrapNodes();
        assertTrue(bootstrap.isEmpty());
    }

    @Test
    public void cmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvancedRolesSettings.CMSSettings cmsSettings = settings.getSettingsObj().getRoles().getCms();
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

        SettingsConfiguration.Settings.AdvancedRolesSettings.DDSSettings ddsSettings = settings.getSettingsObj().getRoles().getDds();
        assertFalse(ddsSettings.isExposed());
        assertEquals(ddsSettings.getCacheFile(), "manifests.cache");
        assertEquals(ddsSettings.getIndexFile(), "dds.index");
    }

    @Test
    public void rmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvancedRolesSettings.RMSSettings rmsSettings = settings.getSettingsObj().getRoles().getRms();
        assertTrue(rmsSettings.isExposed());
        assertEquals(rmsSettings.getCacheFile(), "usro.cache");
    }

    @Test
    public void ndsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvancedRolesSettings.NDSSettings ndsSettings = settings.getSettingsObj().getRoles().getNds();
        assertFalse(ndsSettings.isExposed());
    }

    @Test
    public void mmsTest() throws ConfigurationException {

        SettingsConfiguration settings = new SettingsConfiguration(configFile);

        SettingsConfiguration.Settings.AdvancedRolesSettings.MMSSettings mmsSettings = settings.getSettingsObj().getRoles().getMms();
        assertFalse(mmsSettings.isExposed());
    }

    private void testThreadSettings(SettingsConfiguration.Settings.ThreadSettings threadSettings, int initDelay, int period) {

        assertEquals(threadSettings.getInitialDelay(), initDelay);
        assertEquals(threadSettings.getPeriod(), period);
    }

}
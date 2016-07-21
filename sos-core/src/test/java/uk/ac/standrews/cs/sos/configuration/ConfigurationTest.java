package uk.ac.standrews.cs.sos.configuration;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ConfigurationTest {

    private static final String TEST_RESOURCES_PATH = "src/test/resources/";
    private static final String MOCK_PROPERTIES = "#Mock Properties\n" +
            "db.path=\n" +
            "node.port=8080\n" +
            "storage.access.key=\n" +
            "db.password=\n" +
            "db.username=\n" +
            "node.hostname=\n" +
            "node.guid=6b67f67f31908dd0e574699f163eda2cc117f7f4\n" +
            "keys.folder=\n" +
            "storage.secret.key=\n" +
            "storage.type=\n" +
            "storage.location=\n" +
            "storage.password=\n" +
            "db.hostname=\n" +
            "node.is.client=\n" +
            "db.type=sqlite\n" +
            "storage.username=\n" +
            "node.is.storage=\n" +
            "storage.hostname=\n" +
            "node.is.coordinator=\n";

    @BeforeMethod
    public void setUp() {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        file.delete();
    }

    @Test
    public void testGUIDIsGeneratedInProperties() throws Exception {
        Configuration configuration = new Configuration(TEST_RESOURCES_PATH + "config.properties");
        assertFalse(configuration.getPropertyFromConfig(PropertyKeys.NODE_GUID).isEmpty());
    }

    @Test
    public void testEmptyKey() throws IOException {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        Configuration configuration = new Configuration(TEST_RESOURCES_PATH + "config.properties");
        assertTrue(configuration.getPropertyFromConfig(PropertyKeys.NODE_HOSTNAME).isEmpty());
    }

}
package uk.ac.standrews.cs.sos.configuration;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSConfigurationTest {

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
            "node.is.client=true\n" +
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
    public void testFail() throws Exception {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        SOSConfiguration configuration = new SOSConfiguration(file);
        assertNotNull(configuration.getNodeGUID());
    }

    @Test
    public void testEmptyKey() throws IOException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        assertTrue(configuration.getNodeHostname().isEmpty());
    }

    @Test
    public void testExistingKey() throws IOException, GUIDGenerationException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        assertEquals(configuration.getNodeGUID().toString(),
                "6b67f67f31908dd0e574699f163eda2cc117f7f4");
    }

    @Test
    public void testBooleanValue() throws IOException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        assertTrue(configuration.nodeIsClient());
    }

    @Test
    public void testIntValue() throws IOException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        assertEquals(configuration.getNodePort(), 8080);
    }

}

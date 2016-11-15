package uk.ac.standrews.cs.sos.configuration;

import com.typesafe.config.ConfigException;
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
    private static final String MOCK_PROPERTIES =
            "{\n" +
                    "    \"node\" : {\n" +
                    "        \"guid\" : \"6b67f67f31908dd0e574699f163eda2cc117f7f4\",\n" +
                    "        \"port\" : 8080\n" +
                    "        \"is\" : {\n" +
                    "          \"client\" : true\n" +
                    "          \"storage\" : true\n" +
                    "          \"dds\" : true\n" +
                    "          \"nds\" : true\n" +
                    "          \"mcs\" : true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n";

    @BeforeMethod
    public void setUp() {
        File file = new File(TEST_RESOURCES_PATH + "config.conf");
        file.delete();
    }

    @Test
    public void testFail() throws Exception {
        File file = new File(TEST_RESOURCES_PATH + "config.conf");
        SOSConfiguration configuration = new SOSConfiguration(file);
        assertNotNull(configuration.getNodeGUID());
    }

    @Test (expectedExceptions = ConfigException.Missing.class)
    public void testEmptyKey() throws IOException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.conf");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        configuration.getNodeHostname();
    }

    @Test
    public void testExistingKey() throws IOException, GUIDGenerationException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.conf");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        assertEquals(configuration.getNodeGUID().toString(),
                "6b67f67f31908dd0e574699f163eda2cc117f7f4");
    }

    @Test
    public void testBooleanValue() throws IOException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.conf");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        assertTrue(configuration.nodeIsAgent());
    }

    @Test
    public void testIntValue() throws IOException, SOSConfigurationException {
        File file = new File(TEST_RESOURCES_PATH + "config.conf");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        SOSConfiguration configuration = new SOSConfiguration(file);
        assertEquals(configuration.getNodePort(), 8080);
    }

}

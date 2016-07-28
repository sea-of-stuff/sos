package uk.ac.standrews.cs.sos.SOSImpl.Storage;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageTest extends SetUpTest {

    protected Storage storage;

    private static final String TEST_RESOURCES_PATH = "src/test/resources/";
    private static final String MOCK_PROPERTIES = "#Mock Properties\n" +
            "db.path=~/sos/db/dump.db\n" +
            "node.port=8080\n" +
            "storage.access.key=\n" +
            "db.password=\n" +
            "db.username=\n" +
            "node.hostname=\n" +
            "node.guid=6b67f67f31908dd0e574699f163eda2cc117f7f4\n" +
            "keys.folder=~/sos/keys/\n" +
            "storage.secret.key=\n" +
            "storage.type=local\n" +
            "storage.location=/sos/\n" +
            "storage.password=\n" +
            "db.hostname=\n" +
            "node.is.client=false\n" +
            "db.type=sqlite\n" +
            "storage.username=\n" +
            "node.is.storage=true\n" +
            "storage.hostname=\n" +
            "node.is.discovery.data=false\n" +
            "node.is.discovery.node=false\n";

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        storage = localSOSNode.getStorage();
    }

    @Override
    protected void createConfiguration() throws SOSConfigurationException, IOException {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        configuration = new SOSConfiguration(file);
    }

}

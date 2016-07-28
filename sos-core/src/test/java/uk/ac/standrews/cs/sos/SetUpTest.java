package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.index.IndexException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected SOSLocalNode localSOSNode;
    protected InternalStorage internalStorage;
    protected Index index;

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
            "node.is.client=true\n" +
            "db.type=sqlite\n" +
            "storage.username=\n" +
            "node.is.storage=false\n" +
            "storage.hostname=\n" +
            "node.is.discovery.data=false\n" +
            "node.is.dns=false\n";

    protected SOSConfiguration configuration;

    @BeforeMethod
    public void setUp() throws Exception {
        createConfiguration();

        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            internalStorage =
                    new InternalStorage(StorageFactory
                            .createStorage(storageType, root, true)); // FIXME - storage have very different behaviours if mutable or not
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        try {
            index = LuceneIndex.getInstance(internalStorage);
        } catch (IndexException e) {
            throw  new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        localSOSNode = builder.configuration(configuration)
                                .index(index)
                                .internalStorage(internalStorage)
                                .build();
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException, InterruptedException, DataStorageException {
        index.flushDB();
        index.killInstance();

        internalStorage.destroy();
    }

    protected void createConfiguration() throws SOSConfigurationException, IOException {
        File file = new File(TEST_RESOURCES_PATH + "config.properties");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        configuration = new SOSConfiguration(file);
    }
}

package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest extends CommonTest {

    protected SOSLocalNode localSOSNode;
    protected LocalStorage localStorage;

    private static final String TEST_RESOURCES_PATH = "src/test/resources/";
    private static final String MOCK_PROPERTIES =
            "{\n" +
                    "    \"node\" : {\n" +
                    "        \"guid\" : \"3c9bfd93ab9a6e2ed501fc583685088cca66bac2\"\n" +
                    "        \"port\" : 8080\n" +
                    "        \"hostname\" : \"\"\n" +
                    "        \"is\" : {\n" +
                    "            \"agent\" : true\n" +
                    "            \"storage\" : false\n" +
                    "            \"dds\" : false\n" +
                    "            \"nds\" : false\n" +
                    "            \"mcs\" : false\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    \"db\" : {\n" +
                    "        \"filename\" : \"~/sos/db/dump.db\"\n" +
                    "    }\n" +
                    "\n" +
                    "    \"storage\" : {\n" +
                    "        \"type\" : \"local\"\n" +
                    "        \"location\" : \"~/sos/\"\n" +
                    "    }\n" +
                    "\n" +
                    "    \"keys\" : {\n" +
                    "        \"folder\" : \"~/sos/keys/\"\n" +
                    "    }\n" +
                    "\n" +
                    "    \"policy\" : {\n" +
                    "        \"replication\" : {\n" +
                    "            \"factor\" : 0\n" +
                    "        }\n" +
                    "        \"manifest\" : {\n" +
                    "            \"local\" : true\n" +
                    "            \"remote\" : false\n" +
                    "            \"replication\" : 0\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    \"bootstrap\" : []\n" +
                    "}";

    protected SOSConfiguration configuration;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        createConfiguration();

        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            localStorage =
                    new LocalStorage(StorageFactory
                            .createStorage(storageType, root));
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }


        PolicyManager policyManager = configuration.getPolicyManager();

        List<Node> bootstrapNodes = configuration.getBootstrapNodes();

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        localSOSNode = builder.configuration(configuration)
                                .internalStorage(localStorage)
                                .policies(policyManager)
                                .bootstrapNodes(bootstrapNodes)
                                .build();
    }

    @AfterMethod
    public void tearDown() throws IOException, InterruptedException, DataStorageException {
        localStorage.destroy();
    }

    protected void createConfiguration() throws SOSConfigurationException, IOException {
        File file = new File(TEST_RESOURCES_PATH + "config-setup.conf");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        configuration = new SOSConfiguration(file);
    }
}

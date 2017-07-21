package uk.ac.standrews.cs.sos;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;

import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest extends CommonTest {

    protected SOSLocalNode localSOSNode;
    protected LocalStorage localStorage;

    private static final String MOCK_PROPERTIES =
            "{\n" +
                    "    \"node\" : {\n" +
                    "        \"guid\" : \"3c9bfd93ab9a6e2ed501fc583685088cca66bac2\",\n" +
                    "        \"port\" : 8080,\n" +
                    "        \"hostname\" : \"\",\n" +
                    "        \"is\" : {\n" +
                    "            \"agent\" : true,\n" +
                    "            \"storage\" : false,\n" +
                    "            \"dds\" : false,\n" +
                    "            \"nds\" : false,\n" +
                    "            \"mms\" : false,\n" +
                    "            \"cms\" : false,\n" +
                    "            \"rms\" : false\n" +
                    "        }\n" +
                    "    },\n" +
                    "\n" +
                    "    \"db\" : {\n" +
                    "        \"filename\" : \"dump.db\"\n" +
                    "    },\n" +
                    "\n" +
                    "    \"storage\" : {\n" +
                    "        \"type\" : \"local\",\n" +
                    "        \"location\" : \"~/sos/\"\n" +
                    "    },\n" +
                    "\n" +
                    "    \"keys\" : {\n" +
                    "        \"folder\" : \"~/sos/keys/\"\n" +
                    "    },\n" +
                    "\n" +
                    "    \"policy\" : {\n" +
                    "        \"replication\" : {\n" +
                    "            \"factor\" : 0\n" +
                    "        },\n" +
                    "        \"manifest\" : {\n" +
                    "            \"local\" : true,\n" +
                    "            \"remote\" : false,\n" +
                    "            \"replication\" : 0\n" +
                    "        }\n" +
                    "    },\n" +
                    "\n" +
                    "    \"bootstrap\" : []\n" +
                    "}";

    protected SettingsConfiguration.Settings settings;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        createConfiguration();

        try {
            CastoreBuilder castoreBuilder = settings.getStore().getCastoreBuilder();
            IStorage stor = CastoreFactory.createStorage(castoreBuilder);
            localStorage = new LocalStorage(stor);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        List<SettingsConfiguration.Settings.NodeSettings> bootstrapNodes = settings.getBootstrapNodes();

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        localSOSNode = builder.settings(settings)
                                .internalStorage(localStorage)
                                .bootstrapNodes(bootstrapNodes)
                                .build();
    }

    @AfterMethod
    public void tearDown() throws IOException, InterruptedException, DataStorageException {
        localStorage.destroy();

        FileUtils.deleteDirectory(new File(System.getProperty("user.home") + "/sos/keys/"));

        localSOSNode.kill();
    }

    protected void createConfiguration() throws ConfigurationException, IOException {
        File file = new File(TEST_RESOURCES_PATH + "config-setup.conf");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        settings = new SettingsConfiguration(file).getSettingsObj();
    }
}

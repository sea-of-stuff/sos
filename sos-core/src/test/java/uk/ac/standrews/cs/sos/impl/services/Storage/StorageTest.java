package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.services.Storage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageTest extends SetUpTest {

    protected Storage storage;

    private static final String TEST_RESOURCES_PATH = "src/test/resources/";
    private static final String MOCK_PROPERTIES =
            "{\n" +
                    "    \"node\" : {\n" +
                    "        \"guid\" : \"3c9bfd93ab9a6e2ed501fc583685088cca66bac2\",\n" +
                    "        \"port\" : 8080,\n" +
                    "        \"hostname\" : \"\",\n" +
                    "        \"is\" : {\n" +
                    "            \"agent\" : false,\n" +
                    "            \"storage\" : true,\n" +
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
                    "    \"bootstrap\" : [\n" +
                    "        {\n" +
                    "            \"guid\" : \"6b67f67f31908dd0e574699f163eda2cc117f7f4\",\n" +
                    "            \"port\" : 8080,\n" +
                    "            \"hostname\" : \"cs-wifi-174.cs.st-andrews.ac.uk\",\n" +
                    "            \"is\" : {\n" +
                    "                \"agent\" : false,\n" +
                    "                \"storage\" : true,\n" +
                    "                \"dds\" : false,\n" +
                    "                \"nds\" : false,\n" +
                    "                \"mms\" : false,\n" +
                    "                \"cms\" : false,\n" +
                    "                \"rms\" : false\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        storage = localSOSNode.getStorage();
    }

    @Override
    protected void createConfiguration() throws ConfigurationException, IOException {
        File file = new File(TEST_RESOURCES_PATH + "config-storage.conf");
        Files.write(file.toPath(), MOCK_PROPERTIES.getBytes());

        System.out.println(MOCK_PROPERTIES);
        settings = new SettingsConfiguration(file).getSettingsObj();
    }

}

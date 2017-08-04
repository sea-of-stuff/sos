package uk.ac.standrews.cs.sos.impl.services.Client.replication;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.SOSConstants;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.services.Agent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClientReplicationTest extends SetUpTest {

    protected Agent agent;

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 8110;

    protected static final String TEST_DATA = "test-data";
    private static final String NODE_ID = "SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        agent = localSOSNode.getAgent();

        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/storage/stream")
                                .withBody(TEST_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody(
                                        "{\n" +
                                                "    \"" + SOSConstants.MANIFEST + "\" : \n" +
                                                "    {\n" +
                                                "        \"Type\" : \"Atom\",\n" +
                                                "        \"ContentGUID\" : \"" + testGUID.toMultiHash() + "\",\n" +
                                                "        \"Locations\" : \n" +
                                                "        [\n" +
                                                "              {\n" +
                                                "                \"Type\" : \"persistent\",\n" +
                                                "                \"Location\" : \"sos://" + NODE_ID + "/" + testGUID.toMultiHash() + "\"\n" +
                                                "            } \n" +
                                                "        ]\n" +
                                                "    }\n" +
                                                "}"
                                )
                );

        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/storage/data/guid/" + testGUID.toMultiHash())
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody(TEST_DATA)
                );
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Override
    protected void createConfiguration() throws ConfigurationException, IOException {
        File file = new File(TEST_CONFIGURATIONS_PATH + "config_storage.json");

        settings = new SettingsConfiguration(file).getSettingsObj();
    }

}

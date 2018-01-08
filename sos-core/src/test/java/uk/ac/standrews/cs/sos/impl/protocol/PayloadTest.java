package uk.ac.standrews.cs.sos.impl.protocol;

import org.apache.xmlbeans.impl.util.Base64;
import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.BasicNode;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.Payload;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PayloadTest extends ProtocolTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10006;

    private static final String TEST_DATA = "test-data";

    private static byte[] data_l = new byte[5000000]; // 5mb
    private static byte[] data_l_b64;
    static {
        new Random().nextBytes(data_l);
        data_l_b64 = Base64.encode(data_l);
    }
    private static final String LARGE_TEST_DATA= new String(data_l_b64);

    @BeforeMethod
    public void setUp() throws ConfigurationException, GUIDGenerationException, IOException, SOSException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/fetch_data_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/payload/")
                                .withBody(TEST_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/payload/")
                                .withBody(LARGE_TEST_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicPayloadTask() {

        Node nodeToPing = new BasicNode("localhost", MOCK_SERVER_PORT);
        Payload payload = new Payload(nodeToPing, IO.StringToInputStream(TEST_DATA));
        TasksQueue.instance().performSyncTask(payload);

        assertEquals(payload.getState(), TaskState.SUCCESSFUL);
    }

    @Test
    public void largePayloadTask() {

        Node nodeToPing = new BasicNode("localhost", MOCK_SERVER_PORT);
        Payload payload = new Payload(nodeToPing, IO.StringToInputStream(LARGE_TEST_DATA));
        TasksQueue.instance().performSyncTask(payload);

        assertEquals(payload.getState(), TaskState.SUCCESSFUL);
    }

}

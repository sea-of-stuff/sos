package uk.ac.standrews.cs.sos.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplicationWithDDSFeedbackTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10003;

    private static final String TEST_DATA = "test-data";
    private static final String NODE_ID = "3c9bfd93ab9a6e2ed501fc583685088cca66bac2";

    private static final String RESPONSE_WITH_DDS_INFO =
            "{\n" +
                    "\t\"Manifest\" : \n" +
                    "\t{\n" +
                    "\t\t\"Type\" : \"Atom\",\n" +
                    "\t\t\"ContentGUID\" : \"da39a3ee5e6b4b0d3255bfef95601890afd80709\",\n" +
                    "\t\t\"Locations\" : \n" +
                    "\t\t[\n" +
                    "\t\t  \t{\n" +
                    "\t\t\t    \"Type\" : \"persistent\",\n" +
                    "\t\t\t    \"Location\" : \"sos://029bfd93ab9a6e2ed501fc583685088cca66bac2/da39a3ee5e6b4b0d3255bfef95601890afd80709\"\n" +
                    "\t\t\t} \n" +
                    "\t\t]\n" +
                    "\t},\n" +
                    "\t\"DDS\" : \n" +
                    "\t[\n" +
                    "\t\t\"aebbfd93ab9a6e2ed501fc583685088cca66bac2\",\n" +
                    "\t\t\"5039a3ee5e6b4b0d3255bfef95601890afd80709\",\n" +
                    "\t\t\"002bfd93ab9a6e2ed501fc583685088cca66bac2\"\n" +
                    "\t]\n" +
                    "}";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

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
                                .withBody(RESPONSE_WITH_DDS_INFO)
                );

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void dummyTest() {
        assertTrue(true);
    }
}

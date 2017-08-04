package uk.ac.standrews.cs.sos.impl.services.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.FetchMetadata;

import java.io.IOException;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchMetadataTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

    private static final String GUID_METADATA = "SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_METADATA =
            "{\n" +
                    "    \"GUID\": \"SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\",\n" +
                    "    \"Properties\": [\n" +
                    "        {\n" +
                    "            \"Key\": \"X-Parsed-By\",\n" +
                    "            \"Type\": \"String\",\n" +
                    "            \"Value\": \"org.apache.tika.parser.DefaultParser\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"Key\": \"Content-Encoding\",\n" +
                    "            \"Type\": \"String\",\n" +
                    "            \"Value\": \"null\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"Key\": \"Size\",\n" +
                    "            \"Type\": \"int\",\n" +
                    "            \"Value\": 26\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"Key\": \"Timestamp\",\n" +
                    "            \"Type\": \"int\",\n" +
                    "            \"Value\": 1484736105\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"Key\": \"Content-Type\",\n" +
                    "            \"Type\": \"String\",\n" +
                    "            \"Value\": \"text/plain; charset=ISO-8859-1\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.recreateGUID(GUID_METADATA);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/mms/metadata/guid/" + testGUID.toMultiHash())
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(TEST_METADATA)
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test(timeOut = 10000)
    public void basicMetadataFetchTest() throws IOException, GUIDGenerationException, SOSURLException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, true, false, false);

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_METADATA);

        FetchMetadata fetchMetadata = new FetchMetadata(node, testGUID);
        TasksQueue.instance().performSyncTask(fetchMetadata);

        Metadata metadata = fetchMetadata.getMetadata();
        assertNotNull(metadata);
        assertEquals(metadata.guid(), testGUID);
    }
}

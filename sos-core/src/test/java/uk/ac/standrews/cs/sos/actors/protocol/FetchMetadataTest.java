package uk.ac.standrews.cs.sos.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.node.SOSNode;

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

    private static final String GUID_METADATA = "bcc6949885b1d0db8a749c357f2ffebc167917af";
    private static final String TEST_METADATA =
            "{\n" +
                    "  \"GUID\" : \"bcc6949885b1d0db8a749c357f2ffebc167917af\",\n" +
                    "  \"Properties\" : [ {\n" +
                    "    \"Key\" : \"X-Parsed-By\",\n" +
                    "    \"Value\" : \"org.apache.tika.parser.DefaultParser\"\n" +
                    "  }, {\n" +
                    "    \"Key\" : \"Size\",\n" +
                    "    \"Value\" : \"26\"\n" +
                    "  }, {\n" +
                    "    \"Key\" : \"Content-Encoding\",\n" +
                    "    \"Value\" : null\n" +
                    "  }, {\n" +
                    "    \"Key\" : \"Timestamp\",\n" +
                    "    \"Value\" : \"1484736105\"\n" +
                    "  }, {\n" +
                    "    \"Key\" : \"Content-Type\",\n" +
                    "    \"Value\" : \"text/plain; charset=ISO-8859-1\"\n" +
                    "  } ]\n" +
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
                                .withPath("/dds/metadata/guid/" + testGUID.toString())
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(TEST_METADATA)
                );

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicMetadataFetchTest() throws IOException, GUIDGenerationException, SOSURLException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, true, false, false);

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_METADATA);

        SOSMetadata metadata = FetchMetadata.Fetch(node, testGUID);
        assertNotNull(metadata);
        assertEquals(metadata.guid(), testGUID);
    }
}

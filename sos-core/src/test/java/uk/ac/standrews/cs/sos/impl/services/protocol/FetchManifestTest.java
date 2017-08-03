package uk.ac.standrews.cs.sos.impl.services.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.FetchManifest;

import java.io.IOException;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchManifestTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

    private static final String GUID_VERSION = "SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_VERSION_MANIFEST = "{\"Type\":\"Version\"," +
            "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "\"GUID\":\""+ GUID_VERSION+"\"," +
            "\"Signature\":\"AAAB\"," +
            "\"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
            "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
            "}}";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/dds/manifest/guid/" + GUID_VERSION)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(TEST_VERSION_MANIFEST)
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicManifestFetchTest() throws IOException, GUIDGenerationException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256),
                "localhost", MOCK_SERVER_PORT,
                false, false, true, false, false, false, false);

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_VERSION);

        FetchManifest fetchManifest = new FetchManifest(node, testGUID);
        TasksQueue.instance().performSyncTask(fetchManifest);

        Manifest manifest = fetchManifest.getManifest();
        assertNotNull(manifest);
        assertEquals(manifest.getType(), ManifestType.VERSION);
        assertEquals(manifest.guid(), testGUID);
    }

}

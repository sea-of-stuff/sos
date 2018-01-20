package uk.ac.standrews.cs.sos.impl.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.FetchManifest;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.File;
import java.io.IOException;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchManifestTest extends ProtocolTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

    private static final String GUID_VERSION = "SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_VERSION_MANIFEST = "" +
            "{" +
            "  \"type\":\"Version\"," +
            "  \"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"GUID\":\""+ GUID_VERSION+"\"," +
            "  \"Signature\":\"AAAB\"," +
            "  \"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
            "  \"Content\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
            "}";

    @BeforeMethod
    public void setUp() throws ConfigurationException, GUIDGenerationException, SOSException, IOException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/fetch_manifest_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/sos/mds/manifest/guid/" + GUID_VERSION)
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

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, false, true, false, false, false, false, false);

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_VERSION);

        FetchManifest fetchManifest = new FetchManifest(node, testGUID);
        TasksQueue.instance().performSyncTask(fetchManifest);

        Manifest manifest = fetchManifest.getManifest();
        assertNotNull(manifest);
        assertEquals(manifest.getType(), ManifestType.VERSION);
        assertEquals(manifest.guid(), testGUID);
    }

}

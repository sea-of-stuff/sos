package uk.ac.standrews.cs.sos.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
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
public class FetchManifestTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

    private static final String GUID_VERSION = "28b7f98d7163d2ef91b3a418316246ba2d76b353";
    private static final String TEST_ASSET_MANIFEST = "{\"Type\":\"Asset\"," +
            "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "\"Version\":\""+ GUID_VERSION+"\"," +
            "\"Signature\":\"AAAB\"," +
            "\"Metadata\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
            "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
            "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
            "}}";;

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.recreateGUID(GUID_VERSION);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/dds/manifest/guid/" + testGUID)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody(TEST_ASSET_MANIFEST)
                );

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicManifestFetchTest() throws IOException, GUIDGenerationException, SOSURLException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, true, false, false);

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_VERSION);

        Manifest manifest = FetchManifest.Fetch(node, testGUID);
        assertNotNull(manifest);
        assertEquals(manifest.getManifestType(), ManifestType.ASSET);
        assertEquals(manifest.guid(), testGUID);
    }

}

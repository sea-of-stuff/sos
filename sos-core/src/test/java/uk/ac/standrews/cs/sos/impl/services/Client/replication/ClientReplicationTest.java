package uk.ac.standrews.cs.sos.impl.services.Client.replication;

import com.adobe.xmp.impl.Base64;
import org.mockserver.integration.ClientAndServer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.PublicKey;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@PrepareForTest(DigitalSignature.class)
public class ClientReplicationTest extends SetUpTest {

    protected Agent agent;

    private ClientAndServer mockServer;
    protected static final int MOCK_SERVER_PORT = 10001;

    protected static final String TEST_DATA = "test-data";
    private static final String TEST_DATA_HASH = "SHA256_16_a186000422feab857329c684e9fe91412b1a5db084100b37a98cfc95b62aa867";

    // This is the exact body request. Would be good if we have a JSON matcher method, so this string does not have to be exact, but simply an equivalent JSON obj of what we expect
    private static final String BASIC_REQUEST = "" +
            "{\n"+
            "  \"metadata\" : {\n"+
            "    \"replicationFactor\" : 0,\n"+
            "    \"replicationNodes\" : {\n"+
            "      \"type\" : \"ANY\",\n"+
            "      \"refs\" : [ ]\n"+
            "    },\n"+
            "    \"protectedData\" : false\n"+
            "  },\n"+
            "  \"data\" : \"{DATA}\",\n"+
            "  \"guid\" : \"" + TEST_DATA_HASH + "\"\n" +
            "}";

    protected static final String NODE_ID = "SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";

    protected NodeDiscoveryService nds;
    protected PublicKey mockSignatureCertificate;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        try {
            mockSignatureCertificate = mock(PublicKey.class);
            PowerMockito.mockStatic(DigitalSignature.class);
            PowerMockito.when(DigitalSignature.verify64(any(PublicKey.class), any(String.class), any(String.class))).thenReturn(true);
            PowerMockito.when(DigitalSignature.getCertificateString(any(PublicKey.class))).thenReturn("CERTIFICATE_MOCK_TEST");
        } catch (CryptoException e) {
            throw new SOSProtocolException("Protocol Mocking errors");
        }

        agent = localSOSNode.getAgent();
        nds = localSOSNode.getNDS();

        IGUID testGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, TEST_DATA);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/storage/stream")
                                .withBody(BASIC_REQUEST.replace("{DATA}", Base64.encode(TEST_DATA)))
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody(
                                        "    {\n" +
                                                "        \"type\" : \"Atom\",\n" +
                                                "        \"guid\" : \"" + testGUID.toMultiHash() + "\",\n" +
                                                "        \"locations\" : \n" +
                                                "        [\n" +
                                                "              {\n" +
                                                "                \"type\" : \"persistent\",\n" +
                                                "                \"location\" : \"sos://" + NODE_ID + "/" + testGUID.toMultiHash() + "\"\n" +
                                                "            } \n" +
                                                "        ]\n" +
                                                "    }\n"
                                )
                );

        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/sos/storage/data/guid/" + testGUID.toMultiHash())
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(TEST_DATA)
                );

        SOSURLProtocol.getInstance().register(null, nds); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() throws InterruptedException, IOException, DataStorageException {
        super.tearDown();

        mockServer.stop();

        // Let the mock server stop cleanly
        Thread.sleep(2000);
    }

    @Override
    protected void createConfiguration() throws ConfigurationException {
        File file = new File(TEST_CONFIGURATIONS_PATH + "config_storage.json");

        settings = new SettingsConfiguration(file).getSettingsObj();
    }

}

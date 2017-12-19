package uk.ac.standrews.cs.sos.impl.protocol;

import org.mockserver.integration.ClientAndServer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.FetchData;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@PrepareForTest(DigitalSignature.class)
public class FetchDataTest extends ProtocolTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

    private static final String TEST_DATA = "test-data";

    @BeforeMethod
    public void setUp() throws ConfigurationException, GUIDGenerationException, IOException, SOSException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/fetch_data_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));

        IGUID testGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, TEST_DATA);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
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

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicDataFetchTest() throws IOException, GUIDGenerationException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false, false);

        IGUID testGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, TEST_DATA);

        FetchData fetchData = new FetchData(node, testGUID);
        TasksQueue.instance().performSyncTask(fetchData);
        InputStream fetchedData = fetchData.getBody();

        String fetchedDataString = IO.InputStreamToString(fetchedData);
        assertEquals(fetchedDataString, TEST_DATA);

        fetchedData.close();
    }

    @Test (expectedExceptions = IOException.class)
    public void fetchDataFromNonStorageNodeTest() throws IOException, GUIDGenerationException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false, false, false, false);

        IGUID testGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, TEST_DATA);

        FetchData fetchData = new FetchData(node, testGUID);
    }

    @Test (expectedExceptions = IOException.class)
    public void fetchANullGUIDTest() throws IOException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false, false);

        FetchData fetchData = new FetchData(node, null);
    }
}

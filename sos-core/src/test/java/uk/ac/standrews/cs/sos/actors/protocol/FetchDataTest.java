package uk.ac.standrews.cs.sos.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.protocol.tasks.FetchData;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.tasks.TasksQueue;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.IOException;
import java.io.InputStream;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchDataTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

    private static final String TEST_DATA = "test-data";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/storage/data/guid/" + testGUID)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(TEST_DATA)
                );

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicDataFetchTest() throws IOException, GUIDGenerationException, SOSURLException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        FetchData fetchData = new FetchData(node, testGUID);
        TasksQueue.instance().performSyncTask(fetchData);
        InputStream fetchedData = fetchData.getBody();

        String fetchedDataString = IO.InputStreamToString(fetchedData);
        assertEquals(fetchedDataString, TEST_DATA);

        fetchedData.close();
    }

    @Test (expectedExceptions = IOException.class)
    public void fetchDataFromNonStorageNodeTest() throws IOException, GUIDGenerationException, SOSURLException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false, false, false);

        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        FetchData fetchData = new FetchData(node, testGUID);
    }

    @Test (expectedExceptions = IOException.class)
    public void fetchANullGUIDTest() throws IOException, GUIDGenerationException, SOSURLException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        FetchData fetchData = new FetchData(node, null);
    }
}

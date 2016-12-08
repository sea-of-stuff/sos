package uk.ac.standrews.cs.sos.network;

import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;
import java.net.URL;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RequestsWithMockServerTest {

    private ClientAndProxy proxy;
    private ClientAndServer mockServer;

    @BeforeMethod
    public void startProxy() {
        mockServer = startClientAndServer(9998);
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/test")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("test body")
                );
    }

    @AfterMethod
    public void stopProxy() {
        mockServer.stop();
    }

    @Test
    public void basicMockServerTest() throws IOException {
        SyncRequest request = new SyncRequest(Method.GET, new URL("http://0.0.0.0:9998/test"));
        RequestsManager.getInstance().playSyncRequest(request);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.getBody());
        assertEquals(responseBody, "test body");
    }
}

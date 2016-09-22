package uk.ac.standrews.cs.sos.network;

import okhttp3.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.LOG;

import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SyncRequestTest {

    @BeforeMethod
    public void setUp() {
        LOG log = new LOG(GUIDFactory.generateRandomGUID());
    }

    @Test
    public void testGet() throws Exception {
        RequestsManager requestsManager = new RequestsManager();

        SyncRequest request = new SyncRequest(Method.GET, new URL("https://httpbin.org/range/10"));
        requestsManager.playRequest(request);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.body().byteStream());
        assertEquals(responseBody, "abcdefghij");
    }

    @Test
    public void testGetOKAYRespondeCode() throws Exception {
        int testCode = 418;

        RequestsManager requestsManager = new RequestsManager();

        SyncRequest request = new SyncRequest(Method.GET, new URL("https://httpbin.org/status/" + testCode));
        requestsManager.playRequest(request);

        int code = request.getRespondeCode();
        assertEquals(code, testCode);
    }

    @Test
    public void testPostJSON() throws Exception {

    }

    @Test
    public void testGetResponse() throws Exception {

    }

    @Test
    public void testConcurrentGet() {
        // TODO
    }

}
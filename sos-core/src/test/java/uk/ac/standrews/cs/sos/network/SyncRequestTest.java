package uk.ac.standrews.cs.sos.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.LOG;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        int testCode = 200;
        String dataToPost = "test-data";

        RequestsManager requestsManager = new RequestsManager();
        SyncRequest request = new SyncRequest(Method.POST, new URL("http://httpbin.org/post"));
        request.setJSONBody(dataToPost);
        requestsManager.playRequest(request);

        int code = request.getRespondeCode();
        assertEquals(code, testCode);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.body().byteStream());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readTree(responseBody);
        String postedData = responseJSON.get("data").asText();

        assertEquals(postedData, dataToPost);
    }

    @Test
    public void testPutJSON() throws Exception {
        int testCode = 200;
        String dataToPut = "test-data";

        RequestsManager requestsManager = new RequestsManager();
        SyncRequest request = new SyncRequest(Method.PUT, new URL("http://httpbin.org/put"));
        request.setJSONBody(dataToPut);
        requestsManager.playRequest(request);

        int code = request.getRespondeCode();
        assertEquals(code, testCode);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.body().byteStream());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readTree(responseBody);
        String putData = responseJSON.get("data").asText();

        assertEquals(putData, dataToPut);
    }

    @Test
    public void testConcurrentGet() throws InterruptedException {
        int numberOfThreads = 10;
        int testCode = 418;

        RequestsManager requestsManager = new RequestsManager();

        Runnable r = () -> {
            try {
                SyncRequest request = new SyncRequest(Method.GET, new URL("https://httpbin.org/status/" + testCode));
                requestsManager.playRequest(request);

                int code = request.getRespondeCode();
                assertEquals(code, testCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        for(int i = 0; i < numberOfThreads; i++) {
            executor.submit(r);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

}

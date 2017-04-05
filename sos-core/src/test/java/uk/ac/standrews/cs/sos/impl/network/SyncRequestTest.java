package uk.ac.standrews.cs.sos.impl.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SyncRequestTest extends CommonTest {

    @BeforeMethod
    public void setUp(java.lang.reflect.Method testMethod) throws Exception {
        super.setUp(testMethod);

        new SOS_LOG(GUIDFactory.generateRandomGUID());
    }

    @Test
    public void testGet() throws Exception {
        SyncRequest request = new SyncRequest(Method.GET, new URL("https://httpbin.org/range/10"));
        RequestsManager.getInstance().playSyncRequest(request);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.getBody());
        assertEquals(responseBody, "abcdefghij");
    }

    @Test
    public void testGetOKAYRespondeCode() throws Exception {
        int testCode = 418;

        SyncRequest request = new SyncRequest(Method.GET, new URL("https://httpbin.org/status/" + testCode));
        RequestsManager.getInstance().playSyncRequest(request);

        int code = request.getRespondeCode();
        assertEquals(code, testCode);
    }

    @Test (expectedExceptions = IOException.class)
    public void testPostNull() throws Exception {
        SyncRequest request = new SyncRequest(Method.POST, new URL("http://httpbin.org/post"));
        request.setJSONBody(null);
        RequestsManager.getInstance().playSyncRequest(request);
    }

    @Test
    public void testPostJSON() throws Exception {
        int testCode = 200;
        String dataToPost = "test-data";

        SyncRequest request = new SyncRequest(Method.POST, new URL("http://httpbin.org/post"));
        request.setJSONBody(dataToPost);
        RequestsManager.getInstance().playSyncRequest(request);

        int code = request.getRespondeCode();
        assertEquals(code, testCode);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readTree(responseBody);
        String postedData = responseJSON.get("data").asText();

        assertEquals(postedData, dataToPost);
    }

    @Test (expectedExceptions = IOException.class)
    public void testPutNull() throws Exception {
        SyncRequest request = new SyncRequest(Method.PUT, new URL("http://httpbin.org/post"));
        request.setJSONBody(null);
        RequestsManager.getInstance().playSyncRequest(request);
    }

    @Test
    public void testPutJSON() throws Exception {
        int testCode = 200;
        String dataToPut = "test-data";

        SyncRequest request = new SyncRequest(Method.PUT, new URL("http://httpbin.org/put"));
        request.setJSONBody(dataToPut);
        RequestsManager.getInstance().playSyncRequest(request);

        int code = request.getRespondeCode();
        assertEquals(code, testCode);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readTree(responseBody);
        String putData = responseJSON.get("data").asText();

        assertEquals(putData, dataToPut);
    }

    @Test
    public void testConcurrentGet() throws InterruptedException {
        int numberOfThreads = 10;
        int testCode = 418;

        Runnable r = () -> {
            try {
                SyncRequest request = new SyncRequest(Method.GET, new URL("https://httpbin.org/status/" + testCode));
                RequestsManager.getInstance().playSyncRequest(request);

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

    @Test
    public void testPostData() throws IOException {
        int testCode = 200;
        String dataToPost = "test-data";

        SyncRequest request = new SyncRequest(Method.POST, new URL("http://httpbin.org/post"));
        InputStream dataStream = HelperTest.StringToInputStream(dataToPost);
        request.setBody(dataStream);
        RequestsManager.getInstance().playSyncRequest(request);

        int code = request.getRespondeCode();
        assertEquals(code, testCode);

        Response response = request.getResponse();
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readTree(responseBody);
        String postedData = responseJSON.get("data").asText();

        assertEquals(postedData, dataToPost);
    }

}

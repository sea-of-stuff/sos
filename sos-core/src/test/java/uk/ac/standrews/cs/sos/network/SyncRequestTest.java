/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.network;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SyncRequestTest extends CommonTest {

    @BeforeMethod
    public void setUp(java.lang.reflect.Method testMethod) throws Exception {
        super.setUp(testMethod);

        new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));

        // Read the settings configuration. This will set the SSL Store path.
        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_CONFIGURATIONS_PATH + "config_network.json")).getSettingsObj();
    }

    @Test
    public void testGet() throws Exception {
        SyncRequest request = new SyncRequest(HTTPMethod.GET, new URL("http://httpbin.org/range/10"), ResponseType.TEXT);
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        assertNotNull(response);

        String responseBody = response.getStringBody();
        assertEquals(responseBody, "abcdefghij");
    }

    @Test
    public void testGetHTTPS() throws Exception {
        SyncRequest request = new SyncRequest(HTTPMethod.GET, new URL("https://httpbin.org/range/10"), ResponseType.TEXT);
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        assertNotNull(response);

        String responseBody = response.getStringBody();
        assertEquals(responseBody, "abcdefghij");
    }

    @Test
    public void testGetHTTPSOnImage() throws Exception {
        SyncRequest request = new SyncRequest(HTTPMethod.GET, new URL("https://www.takemefishing.org/tmf/assets/images/fish/american-shad-464x170.png"), ResponseType.BINARY);
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        assertNotNull(response);

        assertEquals(response.getCode(), 200);
        assertEquals(response.getContentLength(), 100844); // ~ 100KB
    }


    @Test
    public void testGetOKAYRespondeCode() throws Exception {
        int testCode = 418;

        SyncRequest request = new SyncRequest(HTTPMethod.GET, new URL("http://httpbin.org/status/" + testCode), ResponseType.TEXT);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        int code = response.getCode();
        assertEquals(code, testCode);
    }

    @Test (expectedExceptions = IOException.class)
    public void testPostNull() throws Exception {
        SyncRequest request = new SyncRequest(HTTPMethod.POST, new URL("http://httpbin.org/post"));
        request.setJSONBody(null);
        RequestsManager.getInstance().playSyncRequest(request);
    }

    @Test
    public void testPostJSON() throws Exception {
        int testCode = 200;
        String dataToPost = "test-data";

        SyncRequest request = new SyncRequest(HTTPMethod.POST, new URL("http://httpbin.org/post"), ResponseType.JSON);
        request.setJSONBody(dataToPost);
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        assertNotNull(response);

        int code = response.getCode();
        assertEquals(code, testCode);

        JsonNode responseJSON = response.getJSON();
        String postedData = responseJSON.get("data").asText();

        assertEquals(postedData, dataToPost);
    }

    @Test (expectedExceptions = IOException.class)
    public void testPutNull() throws Exception {
        SyncRequest request = new SyncRequest(HTTPMethod.PUT, new URL("http://httpbin.org/post"));
        request.setJSONBody(null);
        RequestsManager.getInstance().playSyncRequest(request);
    }

    @Test
    public void testPutJSON() throws Exception {
        int testCode = 200;
        String dataToPut = "test-data";

        SyncRequest request = new SyncRequest(HTTPMethod.PUT, new URL("http://httpbin.org/put"), ResponseType.JSON);
        request.setJSONBody(dataToPut);
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        assertNotNull(response);

        int code = response.getCode();
        assertEquals(code, testCode);

        JsonNode responseJSON = response.getJSON();
        String putData = responseJSON.get("data").asText();

        assertEquals(putData, dataToPut);
    }

    @Test
    public void testConcurrentGet() throws InterruptedException {
        int numberOfThreads = 10;
        int testCode = 418;

        Runnable r = () -> {
            try {
                SyncRequest request = new SyncRequest(HTTPMethod.GET, new URL("http://httpbin.org/status/" + testCode));
                Response response = RequestsManager.getInstance().playSyncRequest(request);

                int code = response.getCode();
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

        SyncRequest request = new SyncRequest(HTTPMethod.POST, new URL("http://httpbin.org/post"), ResponseType.JSON);
        request.setContent_type("application/json");
        InputStream dataStream = HelperTest.StringToInputStream(dataToPost);
        request.setBody(dataStream);
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        assertNotNull(response);

        int code = response.getCode();
        assertEquals(code, testCode);

        JsonNode responseJSON = response.getJSON();
        String postedData = responseJSON.get("data").asText();

        assertEquals(postedData, dataToPost);
    }

}

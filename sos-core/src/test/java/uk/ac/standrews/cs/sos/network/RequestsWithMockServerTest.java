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

    private ClientAndServer mockServer;

    @BeforeMethod
    public void startProxy() {
        mockServer = startClientAndServer(9998);
        mockServer.dumpToLog();
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
        SyncRequest request = new SyncRequest(HTTPMethod.GET, new URL("http://0.0.0.0:9998/test"));
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        assertNotNull(response);

        String responseBody = HelperTest.InputStreamToString(response.getBody());
        assertEquals(responseBody, "test body");
    }
}

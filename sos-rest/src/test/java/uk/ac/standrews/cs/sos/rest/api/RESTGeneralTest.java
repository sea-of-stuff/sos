/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module rest.
 *
 * rest is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * rest is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rest. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.rest.api;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.utils.IO;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTGeneralTest extends CommonRESTTest {

    private static final String TEST_NODE_INFO = "" +
            "{\n"+
                    /* "  \"guid\" : \"SHA256_16_c96706034e6edca2c7dd5e5f625b33ed40b9f70978499be892db22ccac4bccd6\",\n"+ */
                    "  \"type\" : \"Node\",\n"+
                    /*"  \"certificate\" : \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ+g1RiPTeaFIiw1LZwogFCwa3Cd4ECVMNKzG9lVxI0yonvj8mRyH4Z1/3WaoQTjuZ/lUGU43L24zpsmlxOcUkUCAwEAAQ==\",\n"+*/
                    /*"  \"hostname\" : \"138.251.195.151\",\n"+ */
                    "  \"port\" : 8080,\n"+
                    "  \"services\" : {\n"+
                    "    \"storage\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    },\n"+
                    "    \"cms\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    },\n"+
                    "    \"mds\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    },\n"+
                    "    \"nds\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    },\n"+
                    "    \"rms\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    },\n"+
                    "    \"mms\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    }\n"+
                    "  }\n"+
                    "}\n";

    @Test
    public void testGetInfo() throws Exception {

        Response response = target("/sos/info").request().get();
        assertEquals(response.getStatus(), HTTPStatus.OK);

        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), false);

        response.close();
    }

    @Test
    public void testGetPing() {

        Response response = target("/sos/ping/").request().get();
        assertEquals(response.getStatus(), HTTPStatus.OK);

        System.out.println(response.readEntity(String.class));
        assertEquals(response.readEntity(String.class), "Pong");

        response.close();
    }

    @Test
    public void testGetPingHello() {

        Response response = target("/sos/ping/hello").request().get();
        assertEquals(response.getStatus(), HTTPStatus.OK);

        System.out.println(response.readEntity(String.class));
        assertEquals(response.readEntity(String.class), "Pong: hello");

        response.close();
    }

    @Test
    public void testPostPayload() {

        InputStream testData = IO.StringToInputStream("test-body");

        Response response = target("/sos/payload/").request()
                .post(Entity.entity(testData, MediaType.MULTIPART_FORM_DATA));

        assertEquals(response.getStatus(), HTTPStatus.OK);

        System.out.println(response.readEntity(String.class));
        assertEquals(response.readEntity(String.class), "Data received");

        response.close();
    }

}

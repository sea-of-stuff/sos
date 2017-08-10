package uk.ac.standrews.cs.sos.rest;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.HTTP.HTTPStatus;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTGeneralTest extends CommonRESTTest {

    private static final String TEST_NODE_INFO = "" +
            "{\n" +
            "  \"guid\" : \"SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\",\n" +
            /* "  \"hostname\" : \"Simones-MacBook-Pro.local\",\n" + */ // Ignoring the hostname, as this depends on the running machine
            "  \"port\" : 8080,\n" +
            "  \"services\" : {\n" +
            "    \"storage\" : {\n" +
            "      \"exposed\" : true\n" +
            "    },\n" +
            "    \"cms\" : {\n" +
            "      \"exposed\" : true\n" +
            "    },\n" +
            "    \"dds\" : {\n" +
            "      \"exposed\" : true\n" +
            "    },\n" +
            "    \"nds\" : {\n" +
            "      \"exposed\" : true\n" +
            "    },\n" +
            "    \"rms\" : {\n" +
            "      \"exposed\" : true\n" +
            "    },\n" +
            "    \"mms\" : {\n" +
            "      \"exposed\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    public void testGetInfo() throws Exception {

        Response response = target("/info").request().get();
        assertEquals(response.getStatus(), HTTPStatus.OK);

        System.out.println(response.readEntity(String.class));
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), false);

        response.close();
    }

}

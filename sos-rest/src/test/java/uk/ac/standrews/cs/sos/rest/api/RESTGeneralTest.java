package uk.ac.standrews.cs.sos.rest.api;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTGeneralTest extends CommonRESTTest {

    private static final String TEST_NODE_INFO = "" +
            "{\n"+
                    /* "  \"GUID\" : \"SHA256_16_c96706034e6edca2c7dd5e5f625b33ed40b9f70978499be892db22ccac4bccd6\",\n"+ */
                    "  \"type\" : \"Node\",\n"+
                    /*"  \"signCert\" : \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ+g1RiPTeaFIiw1LZwogFCwa3Cd4ECVMNKzG9lVxI0yonvj8mRyH4Z1/3WaoQTjuZ/lUGU43L24zpsmlxOcUkUCAwEAAQ==\",\n"+*/
                    /*"  \"hostname\" : \"138.251.195.151\",\n"+ */
                    "  \"port\" : 8080,\n"+
                    "  \"services\" : {\n"+
                    "    \"storage\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    },\n"+
                    "    \"cms\" : {\n"+
                    "      \"exposed\" : true\n"+
                    "    },\n"+
                    "    \"dds\" : {\n"+
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

        System.out.println(response.readEntity(String.class));
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), false);

        response.close();
    }

}

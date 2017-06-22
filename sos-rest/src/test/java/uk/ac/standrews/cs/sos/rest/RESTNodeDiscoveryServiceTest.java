package uk.ac.standrews.cs.sos.rest;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.HTTP.HTTPStatus;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTNodeDiscoveryServiceTest extends CommonRESTTest {

    private final static String TEST_NODE_INFO = "{\n" +
            "    \"guid\": \"3c9bfd93ab9a6e2ed501fc583685088cca66bac2\",\n" +
            "    \"hostname\": \"dyn-209-236.cs.st-andrews.ac.uk\",\n" +
            "    \"port\": 8080,\n" +
            "    \"roles\": {\n" +
            "        \"agent\": true,\n" +
            "        \"storage\": true,\n" +
            "        \"dds\": false,\n" +
            "        \"nds\": false,\n" +
            "        \"mms\": false,\n" +
            "        \"cms\": false,\n" +
            "        \"rms\": false\n" +
            "    }\n" +
            "}\n";

    @Test
    public void testRegister() throws Exception {

        String data = "{\n" +
                "\t\"guid\": \"3c9bfd93ab9a6e2ed501fc583685088cca66bac2\",\n" +
                "\t\"hostname\": \"dyn-209-236.cs.st-andrews.ac.uk\",\n" +
                "\t\"port\": 8080,\n" +
                "\t\"roles\": {\n" +
                "\t\t\"agent\": true,\n" +
                "\t\t\"storage\": true\n" +
                "\t}\n" +
                "}";

        Response response = target("/nds/register")
                .request()
                .post(Entity.json(data));

        assertEquals(response.getStatus(), HTTPStatus.OK);
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), true);

        response.close();
    }

}

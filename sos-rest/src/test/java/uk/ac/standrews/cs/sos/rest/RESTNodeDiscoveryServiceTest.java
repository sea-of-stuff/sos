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

    @Test
    public void testRegister() throws Exception {

        String data = "" +
                "{" +
                "    \"guid\": \"SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"," +
                "    \"hostname\": \"Simones-MacBook-Pro.local\"," +
                "    \"port\": 8080," +
                "    \"services\": {" +
                "        \"storage\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"cms\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"dds\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"nds\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"rms\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"mms\": {" +
                "            \"exposed\": true" +
                "        }" +
                "    }" +
                "}";

        Response response = target("/nds/register")
                .request()
                .post(Entity.json(data));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(data, response.readEntity(String.class), true);

        response.close();
    }

}

package uk.ac.standrews.cs.sos.rest;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.HTTP.HTTPState;
import uk.ac.standrews.cs.sos.RESTConfig;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTNDSTest extends CommonRESTTest {

    private final static String TEST_NODE_INFO = "{ \"guid\" : \"00000ccc6adc7e831ee563a8d0daa230690c296a\", \"hostname\" : \"234:234:20:2\", \"port\" : 8081, \"roles\" : {\"agent\" : false, \"storage\" : true, \"dds\" : true, \"nds\" : true, \"mcs\" : false} }";

    @Override
    protected Application configure() {
        config = new RESTConfig();
        return config.build();
    }

    @Test
    public void testRegister() throws Exception {

        String data = "{\n" +
                "    \"guid\" : \"00000ccc6adc7e831ee563a8d0daa230690c296a\",\n" +
                "\t\"ip\": \"234:234:20:2\",\n" +
                "\t\"port\": 8081,\n" +
                "\t\"agent\": false,\n" +
                "\t\"storage\": true,\n" +
                "\t\"NDS\": true,\n" +
                "\t\"DDS\": true,\n" +
                "\t\"MCS\": false\n" +
                "}";


        Response response = target("/nds/register")
                .request()
                .post(Entity.json(data));

        assertEquals(response.getStatus(), HTTPState.OK);
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), true);

        response.close();
    }

}

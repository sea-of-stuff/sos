package uk.ac.standrews.cs.sos.rest;

import org.glassfish.jersey.test.JerseyTestNg;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.HTTP.HTTPState;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.ServerState;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTGeneralTest extends JerseyTestNg.ContainerPerMethodTest {

    private static final String TEST_NODE_INFO = "{\n" +
            "  \"guid\": \"6b67f67f31908dd0e574699f163eda2cc117f7f4\",\n" +
            "  \"hostname\": \"localhost\",\n" +
            "  \"port\": 8080,\n" +
            "  \"roles\": {\n" +
            "    \"client\": true,\n" +
            "    \"storage\": true,\n" +
            "    \"dds\": false,\n" +
            "    \"nds\": true,\n" +
            "    \"mcs\": false\n" +
            "  }\n" +
            "}";

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        ServerState.init();
    }

    @AfterMethod
    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        ServerState.kill();
    }

    @Override
    protected Application configure() {
        return new RESTConfig().build();
    }

    @Test
    public void testGetInfo() throws Exception {

        Response response = target("/info").request().get();
        assertEquals(response.getStatus(), HTTPState.OK);
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), true);
    }

}
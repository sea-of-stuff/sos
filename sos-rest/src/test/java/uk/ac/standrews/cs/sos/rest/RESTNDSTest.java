package uk.ac.standrews.cs.sos.rest;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.HTTP.HTTPState;
import uk.ac.standrews.cs.sos.json.model.NodeModel;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTNDSTest extends CommonRESTTest {

    @Test
    public void testRegister() throws Exception {

        NodeModel node = new NodeModel();
        node.setGuid("ccc6adc7e831ee563a8d0daa230690c2969");
        node.setIp("234:234:20:2");
        node.setPort(8081);
        node.setClient(false);
        node.setNDS(true);
        node.setStorage(true);
        node.setDDS(true);
        node.setMCS(false);

        Entity<NodeModel> entity = Entity.entity(node, MediaType.APPLICATION_JSON);
        Response response = target("/nds/register").request().put(entity);
        assertEquals(response.getStatus(), HTTPState.OK);

        System.out.println(response.readEntity(String.class));
//
//        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), true);
    }

}

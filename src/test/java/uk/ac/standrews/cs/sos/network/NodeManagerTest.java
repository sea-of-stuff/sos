package uk.ac.standrews.cs.sos.network;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.network.roles.Coordinator;

import java.net.InetSocketAddress;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManagerTest {

    @Test(priority=0)
    public void persistTest() throws GUIDGenerationException, DatabasePersistenceException {

        IGUID guid = GUIDFactory.generateGUID("test");
        InetSocketAddress inetSocketAddress = new InetSocketAddress("example.com", 8080);
        Node node = new SOSNode(guid, inetSocketAddress).setNodeRole(new Coordinator()); // TODO - actually this should be external

        NodeManager nodeManager = new NodeManager();
        nodeManager.addNode(node);
        assertEquals(nodeManager.getKnownNodes().size(), 1);

        nodeManager.persist();
        assertEquals(nodeManager.getKnownNodes().size(), 1);
    }

    @Test(priority=1)
    public void getTest() throws GUIDGenerationException, DatabasePersistenceException {

        NodeManager nodeManager = new NodeManager();
        nodeManager.loadFromDB();

        assertEquals(nodeManager.getKnownNodes().size(), 1);
    }

}

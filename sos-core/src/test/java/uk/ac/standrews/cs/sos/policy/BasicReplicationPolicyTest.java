package uk.ac.standrews.cs.sos.policy;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;
import uk.ac.standrews.cs.sos.node.SOSNode;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicReplicationPolicyTest {

    @Test
    public void testGetReplicationFactor() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(3, null);

        assertEquals(replicationPolicy.getReplicationFactor(), 3);
    }

    @Test
    public void testGetZeroReplicationFactor() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(0, null);

        assertEquals(replicationPolicy.getReplicationFactor(), 0);
    }

    @Test
    public void testDoNotGetNegativeReplicationFactor() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(-1, null);

        assertEquals(replicationPolicy.getReplicationFactor(), 0);
    }

    @Test
    public void testAddNode() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(0, null);

        assertEquals(replicationPolicy.getNodes().size(), 0);

        SOSNode mockedNode = mock(SOSNode.class);
        replicationPolicy.addNode(mockedNode);
        assertEquals(replicationPolicy.getNodes().size(), 1);
    }

    @Test
    public void testGetNoNodes() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(0, null);

        assertEquals(replicationPolicy.getNodes().size(), 0);
    }

    @Test
    public void testIsReplicationStrong() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(0, null);

        assertFalse(replicationPolicy.isReplicationStrong());
    }

}

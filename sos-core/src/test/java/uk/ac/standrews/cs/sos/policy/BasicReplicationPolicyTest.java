package uk.ac.standrews.cs.sos.policy;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicReplicationPolicyTest extends CommonTest {

    @Test
    public void testGetReplicationFactor() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(3);

        assertEquals(replicationPolicy.getReplicationFactor(), 3);
    }

    @Test
    public void testGetZeroReplicationFactor() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(0);

        assertEquals(replicationPolicy.getReplicationFactor(), 0);
    }

    @Test
    public void testDoNotGetNegativeReplicationFactor() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(-1);

        assertEquals(replicationPolicy.getReplicationFactor(), 0);
    }

    @Test
    public void testIsReplicationStrong() throws Exception {
        ReplicationPolicy replicationPolicy = new BasicReplicationPolicy(0);

        assertFalse(replicationPolicy.isReplicationStrong());
    }

}

package uk.ac.standrews.cs.sos.policies;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.interfaces.policy.DataReplicationPolicy;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicDataReplicationPolicyTest extends CommonTest {

    @Test
    public void testGetReplicationFactor() throws Exception {
        DataReplicationPolicy dataReplicationPolicy = new BasicDataReplicationPolicy(3);

        assertEquals(dataReplicationPolicy.getReplicationFactor(), 3);
    }

    @Test
    public void testGetZeroReplicationFactor() throws Exception {
        DataReplicationPolicy dataReplicationPolicy = new BasicDataReplicationPolicy(0);

        assertEquals(dataReplicationPolicy.getReplicationFactor(), 0);
    }

    @Test
    public void testDoNotGetNegativeReplicationFactor() throws Exception {
        DataReplicationPolicy dataReplicationPolicy = new BasicDataReplicationPolicy(-1);

        assertEquals(dataReplicationPolicy.getReplicationFactor(), 0);
    }

}

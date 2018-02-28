package uk.ac.standrews.cs.sos.impl.protocol;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomReplicationSequentialTest extends AtomReplicationBaseTest {

    @Test
    public void basicMockServerTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.basicMockServerTest(true);
    }

    @Test
    public void replicateToNoStorageNodeTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateToNoStorageNodeTest(true);
    }

    @Test
    public void replicateOnlyOnceTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateOnlyOnceTest(true);
    }

    @Test
    public void replicateOnlyOnceSecondTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateOnlyOnceSecondTest(true);
    }

    @Test
    public void replicateToSameNodeTwiceTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateToSameNodeTwiceTest(true);
    }

    @Test
    public void replicateSameDataTwiceTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateSameDataTwiceTest(true);
    }

    @Test
    public void basicTimeoutMockServerTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.basicTimeoutMockServerTest(true);
    }
}
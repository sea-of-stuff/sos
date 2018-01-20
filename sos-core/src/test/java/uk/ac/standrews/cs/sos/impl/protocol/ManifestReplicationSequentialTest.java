package uk.ac.standrews.cs.sos.impl.protocol;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplicationSequentialTest extends ManifestReplicationBaseTest {

    @Test
    public void basicVersionManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.basicVersionManifestReplicationTest(true);
    }

    @Test
    public void basicCompoundManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.basicCompoundManifestReplicationTest(true);
    }

    @Test
    public void basicAtomManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException, GUIDGenerationException {
        super.basicAtomManifestReplicationTest(true);
    }

    @Test
    public void basicFATContextManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException, GUIDGenerationException, ManifestNotFoundException, IOException {
        super.basicFATContextManifestReplicationTest(true);
    }

    // Cannot replicate VERSION manifest to noMDS node
    @Test
    public void cannotReplicateManifestToNoMDSNodeReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.cannotReplicateManifestToNoMDSNodeReplicationTest(true);
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void basicManifestReplicationFailsTest() throws SOSProtocolException {
        super.basicManifestReplicationFailsTest(true);
    }

    @Test
    public void badManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.badManifestReplicationTest(true);
    }
}

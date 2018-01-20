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
public class ManifestReplicationParallelTest extends ManifestReplicationBaseTest {

    @Test
    public void basicVersionManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.basicVersionManifestReplicationTest(false);
    }

    @Test
    public void basicCompoundManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.basicCompoundManifestReplicationTest(false);
    }

    @Test
    public void basicAtomManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException, GUIDGenerationException {
        super.basicAtomManifestReplicationTest(false);
    }

    @Test
    public void basicFATContextManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException, GUIDGenerationException, ManifestNotFoundException, IOException {
        super.basicFATContextManifestReplicationTest(false);
    }

    // Cannot replicate VERSION manifest to noMDS node
    @Test
    public void cannotReplicateManifestToNoMDSNodeReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.cannotReplicateManifestToNoMDSNodeReplicationTest(false);
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void basicManifestReplicationFailsTest() throws SOSProtocolException {
        super.basicManifestReplicationFailsTest(false);
    }

    @Test
    public void badManifestReplicationTest() throws SOSProtocolException, NodeNotFoundException {
        super.badManifestReplicationTest(false);
    }
}

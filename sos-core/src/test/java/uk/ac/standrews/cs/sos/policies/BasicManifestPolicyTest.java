package uk.ac.standrews.cs.sos.policies;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicManifestPolicyTest extends CommonTest {

    @Test
    public void testDumpConstructor() {
        ManifestPolicy manifestPolicy= new BasicManifestPolicy(false, false, 0);

        assertFalse(manifestPolicy.storeManifestsLocally());
        assertFalse(manifestPolicy.storeManifestsRemotely());
    }

    @Test
    public void testConstructorLocally() {
        ManifestPolicy manifestPolicy= new BasicManifestPolicy(true, false, 0);

        assertTrue(manifestPolicy.storeManifestsLocally());
        assertFalse(manifestPolicy.storeManifestsRemotely());
    }

    @Test
    public void testConstructorRemotely() {
        ManifestPolicy manifestPolicy= new BasicManifestPolicy(false, true, 0);

        assertFalse(manifestPolicy.storeManifestsLocally());
        assertTrue(manifestPolicy.storeManifestsRemotely());
    }

    @Test
    public void testConstructorLocallyAndRemotely() {
        ManifestPolicy manifestPolicy= new BasicManifestPolicy(true, true, 0);

        assertTrue(manifestPolicy.storeManifestsLocally());
        assertTrue(manifestPolicy.storeManifestsRemotely());
    }

    @Test
    public void testNotNegativeReplicationFactor() {
        ManifestPolicy manifestPolicy= new BasicManifestPolicy(false, false, -1);

        assertEquals(manifestPolicy.getReplicationFactor(), 0);
    }

    @Test
    public void testZeroReplicationFactor() {
        ManifestPolicy manifestPolicy= new BasicManifestPolicy(false, false, 0);

        assertEquals(manifestPolicy.getReplicationFactor(), 0);
    }

    @Test
    public void testPositiveReplicationFactor() {
        ManifestPolicy manifestPolicy= new BasicManifestPolicy(false, false, 3);

        assertEquals(manifestPolicy.getReplicationFactor(), 3);
    }

    @Test
    public void testNotAffectedReplicationFactor() {
        assertEquals(new BasicManifestPolicy(false, false, 3).getReplicationFactor(), 3);
        assertEquals(new BasicManifestPolicy(true, false, 3).getReplicationFactor(), 3);
        assertEquals(new BasicManifestPolicy(false, true, 3).getReplicationFactor(), 3);
        assertEquals(new BasicManifestPolicy(true, true, 3).getReplicationFactor(), 3);

    }
}

package uk.ac.standrews.cs.sos.SOSImpl.Client;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClientTest extends SetUpTest {

    protected Client client;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        client = localSOSNode.getClient();
    }

    @Test(expectedExceptions = ManifestNotFoundException.class)
    public void testFailGetManifest() throws Exception {
        client.getManifest(GUIDFactory.generateRandomGUID());
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void testFailGetManifestNull() throws Exception {
        client.getManifest(null);
    }

}

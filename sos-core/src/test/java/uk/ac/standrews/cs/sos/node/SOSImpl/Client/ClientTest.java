package uk.ac.standrews.cs.sos.node.SOSImpl.Client;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.node.ROLE;
import uk.ac.standrews.cs.sos.node.SOSImpl.SOSNodeTest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClientTest extends SOSNodeTest {

    @Override
    public ROLE nodeRole() {
        return ROLE.CLIENT;
    }

    @Test(expectedExceptions = ManifestNotFoundException.class)
    public void testFailGetManifest() throws Exception {
        model.getManifest(GUIDFactory.recreateGUID("123fa11"));
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void testFailGetManifestNull() throws Exception {
        model.getManifest(null);
    }

}

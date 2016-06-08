package uk.ac.standrews.cs.sos.node.SOS.Storage;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.node.ROLE;
import uk.ac.standrews.cs.sos.node.SOS.SOSNodeTest;
import uk.ac.standrews.cs.sos.utils.Helper;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageTest extends SOSNodeTest {

    @Override
    public ROLE nodeRole() {
        return ROLE.STORAGE;
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testFailGetManifest() throws Exception {
        model.getManifest(GUIDFactory.recreateGUID("123fa11"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testFailAddManifest() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);

        model.addManifest(manifest, false);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testFailVerifyManifest() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);

        model.verifyManifest(model.getIdentity(), manifest);
    }
}

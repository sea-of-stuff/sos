package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.impl.services.SOSStorage;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddSecureAtomTest extends StorageTest {

    @Test
    public void basicAddSecureAtomTest() throws Exception {

        User user = new UserImpl("TEST_ADD_SECURE_ATOM");
        Role role = new RoleImpl(user, "ROLE_TEST_ADD_SECURE_ATOM");

        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        SecureAtomManifest secureAtomManifest = ((SOSStorage) storage).addSecureAtom(builder, role, true);

        assertNotNull(secureAtomManifest.getData(role));
        assertTrue(IOUtils.contentEquals(secureAtomManifest.getData(role), location.getSource()));
    }

}

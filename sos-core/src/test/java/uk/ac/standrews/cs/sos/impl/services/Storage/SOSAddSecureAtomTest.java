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

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddSecureAtomTest extends StorageTest {

    @Test
    public void basicAddSecureAtomTest() throws Exception {

        User user = new UserImpl("TEST_ADD_SECURE_ATOM");
        Role role = new RoleImpl(user, "ROLE_TEST_ADD_SECURE_ATOM");

        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setRole(role);
        SecureAtomManifest secureAtomManifest = ((SOSStorage) storage).addSecureAtom(builder);

        assertNotNull(secureAtomManifest.getData());
        assertFalse(IOUtils.contentEquals(secureAtomManifest.getData().getInputStream(), location.getSource()));
    }

}

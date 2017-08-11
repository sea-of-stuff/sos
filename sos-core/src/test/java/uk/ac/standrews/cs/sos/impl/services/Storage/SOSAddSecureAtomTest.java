package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import static org.testng.AssertJUnit.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddSecureAtomTest extends StorageTest {

    @Test
    public void basicAddSecureAtomTest() throws Exception {

        User user = new UserImpl("TEST_ADD_SECURE_ATOM");
        Role role = new RoleImpl(user, "ROLE_TEST_ADD_SECURE_ATOM");

        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder clearDataBuilder = new AtomBuilder()
                .setLocation(location);
        Atom atomManifest = storage.addAtom(clearDataBuilder);
        assertNotNull(atomManifest.getData());

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setRole(role);
        SecureAtom secureAtomManifest = storage.addSecureAtom(builder);

        assertNotNull(secureAtomManifest.getData());
        assertFalse(IOUtils.contentEquals(secureAtomManifest.getData().getInputStream(), location.getSource()));

        assertNotSame(secureAtomManifest.guid(), atomManifest.guid());
        assertNotSame(secureAtomManifest.getData(), atomManifest.getData());
    }

    @Test
    public void readSecureAtomDataTest() throws Exception {

        User user = new UserImpl("TEST_ADD_SECURE_ATOM");
        Role role = new RoleImpl(user, "ROLE_TEST_ADD_SECURE_ATOM");

        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setRole(role);
        SecureAtom secureAtomManifest = storage.addSecureAtom(builder);

        // TODO - storage.getData(role)
    }

}

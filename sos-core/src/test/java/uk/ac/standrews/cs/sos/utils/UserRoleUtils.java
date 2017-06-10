package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UserRoleUtils {

    public static Role BareRoleMock() throws SignatureException, GUIDGenerationException {
        Role role =  mock(Role.class);
        when(role.sign(any(String.class))).thenReturn("AAAB");
        when(role.guid()).thenReturn(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED));

        return role;
    }

    public static User BareUserMock() {
        return mock(User.class);
    }
}

package uk.ac.standrews.cs.sos.impl.roles;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RoleImplTest {

    @Test
    public void constructorTest() throws SignatureException, ProtectionException {

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        assertEquals(role.getName(), "ROLE_TEST");
        assertNotNull(role.guid());
        assertNotNull(role.getPubKey());
        assertNotNull(role.getSignatureCertificate());
        assertNotNull(role.getSignature());
        assertNotNull(role.getUser());
    }

    @Test
    public void protectTest() throws ProtectionException, SignatureException, CryptoException {

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        SecretKey key = SymmetricEncryption.generateRandomKey();
        String encryptedKey = role.encrypt(key);

        SecretKey decryptedKey = role.decrypt(encryptedKey);
        assertEquals(decryptedKey, key);
    }
}
package uk.ac.standrews.cs.sos.impl.roles;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
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

    @BeforeMethod
    public void setUp() {

        SOSLocalNode.settings = new SettingsConfiguration.Settings();
        SOSLocalNode.settings.setKeys(new SettingsConfiguration.Settings.KeysSettings());
        SOSLocalNode.settings.getKeys().setLocation(System.getProperty("user.home") + "/sos/keys/");
    }

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
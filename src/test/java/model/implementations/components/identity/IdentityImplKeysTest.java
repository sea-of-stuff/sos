package model.implementations.components.identity;

import model.exceptions.DecryptionException;
import model.exceptions.EncryptionException;
import model.exceptions.KeyGenerationException;
import model.interfaces.identity.Identity;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImplKeysTest {

    @Test
    public void testPublicKeyExists() throws KeyGenerationException {
        Identity identity = new IdentityImpl();
        assertNotNull(identity.getPublicKey());

    }

    @Test
    public void testEncryptDecrypt() throws Exception, EncryptionException, DecryptionException {
        Identity identity = new IdentityImpl();

        byte[] encrypted = identity.encrypt("hello");
        String result = identity.decrypt(encrypted);
        assertEquals(result, "hello");
    }

}
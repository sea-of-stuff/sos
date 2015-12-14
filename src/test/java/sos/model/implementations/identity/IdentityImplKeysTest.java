package sos.model.implementations.identity;

import org.testng.annotations.Test;
import sos.exceptions.DecryptionException;
import sos.exceptions.EncryptionException;
import sos.exceptions.KeyGenerationException;
import sos.model.interfaces.identity.Identity;

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
package uk.ac.standrews.cs.sos.utils.crypto;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RSACryptoTest {

    @Test
    public void encryptStringTest() throws KeyGenerationException, EncryptionException {

        // Make sure that it won't fail for strange key lengths
        for(int i = 0; i < 100; i++) {
            RSACrypto rsa = new RSACrypto();
            rsa.generateKeys();

            String encrypted = rsa.encrypt64("PG3F1YKwHXVRsXnmK9hFdA==");
            String decrypted = rsa.decryptToString(encrypted);

            assertEquals(decrypted, "PG3F1YKwHXVRsXnmK9hFdA==");
        }
    }
}

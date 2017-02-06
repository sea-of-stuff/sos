package uk.ac.standrews.cs.sos.utils.crypto;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AESCryptoTest {

    private final String TEST_INPUT = "\n" +
            "02-02-2017 09:14:44 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53450652 [Thread-1] ) ==> ===============================================================\n" +
            "02-02-2017 09:14:44 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53450652 [Thread-1] ) ==> File /argon/data/marcus/status/status.html.tmp was modified\n" +
            "02-02-2017 09:14:44 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53450652 [Thread-1] ) ==> File /argon/data/marcus/status/status.html.tmp won't be tracked\n" +
            "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53774958 [Thread-1] ) ==> ===============================================================\n" +
            "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53774958 [Thread-1] ) ==> File /argon/data/marcus/status/status.html was modified\n" +
            "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53774958 [Thread-1] ) ==> Starting to track file /argon/data/marcus/status/status.html\n" +
            "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.parsers.STAParser  ( 53775046 [Thread-1] ) ==> Starting to read Setup Table entries\n" +
            "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.parsers.STAParser  ( 53775046 [Thread-1] ) ==> Finished to read Setup Table entries\n" +
            "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.parsers.STAParser  ( 53775046 [Thread-1] ) ==> Sta";

    private final String TEST_BASE = "TEST DATA";

    @Test
    public void encrypDecryptBasicTest() throws KeyGenerationException, EncryptionException, IOException {

        AESCrypto aes = new AESCrypto();
        aes.generateKey();

        String encrypted = aes.encrypt64(TEST_BASE);

        String decrypted = aes.decrypt64(encrypted);
        assertEquals(decrypted, TEST_BASE);
    }

    @Test
    public void encrypDecryptTest() throws KeyGenerationException, EncryptionException, IOException {

        // Run this test many times to check that keys are always generated correctly
        for(int i = 0; i < 100; i++) {
            AESCrypto aes = new AESCrypto();
            aes.generateKey();

            String encrypted = aes.encrypt64(TEST_INPUT);
            String decrypted = aes.decrypt64(encrypted);
            assertEquals(decrypted, TEST_INPUT);
        }
    }

    @Test
    public void keyStringTest() throws KeyGenerationException, EncryptionException {

        for(int i = 0; i < 100; i++) {
            AESCrypto aes = new AESCrypto();
            aes.generateKey();

            assertEquals(aes.getKey().length(), 24);
        }
    }

    @Test
    public void encryptStreamTest() throws IOException, KeyGenerationException {
        InputStream inputStream = HelperTest.StringToInputStream(TEST_BASE);

        AESCrypto aes = new AESCrypto();
        aes.generateKey();

        InputStream encrypted = aes.encryptStream(inputStream);
        String encryptedString = HelperTest.InputStreamToString64(encrypted);

        String decrypted = aes.decrypt64(encryptedString);
        assertEquals(decrypted, TEST_BASE);
    }

    @Test
    public void decryptStreamTest() throws IOException, KeyGenerationException, EncryptionException {

        InputStream inputStream = HelperTest.StringToInputStream(TEST_BASE);

        AESCrypto aes = new AESCrypto();
        aes.generateKey();

        InputStream encrypted = aes.encryptStream(inputStream);
        InputStream decryptedStream = aes.decryptStream(encrypted);

        String decrypted = HelperTest.InputStreamToString(decryptedStream);
        assertEquals(decrypted, TEST_BASE);
    }
}
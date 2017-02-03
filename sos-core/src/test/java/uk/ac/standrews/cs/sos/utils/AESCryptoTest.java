package uk.ac.standrews.cs.sos.utils;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.utils.crypto.AESCrypto;

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
    @Test
    public void encrypDecryptTest() throws KeyGenerationException, EncryptionException {

        // Run this test many times to check that keys are always generated correctly
        for(int i = 0; i < 100; i++) {
            AESCrypto aes = new AESCrypto();
            aes.generateKeys();

            String encrypted = aes.encrypt64(TEST_INPUT);
            String decrypted = aes.decryptToString(encrypted);
            assertEquals(decrypted, TEST_INPUT);
        }
    }
}
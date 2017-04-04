package uk.ac.standrews.cs.sos.utils.crypto;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.crypto.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyGenerationException;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AESCryptoTest {

    private final String TEST_INPUT = "" +
            "It is crucial for programmers to understand how long a certain operation takes in and out of a computer. For example, fetching a word from cache, memory, disk, and from other computers.\n" +
            "Inspired by Teach Yourself Programming in Ten Years, I would like to discuss this in a little more detail. (Most information is taken from here)\n" +
            "\n" +
            "The analogy is:\n" +
            "\n" +
            "L1 Cache - There is a sandwich in front of you.\n" +
            "L2 Cache - Walk to the kitchen and make a sandwich\n" +
            "RAM - Drive to the store, purchase sandwich fixings, drive home and make sandwich\n" +
            "Hard Drive - Drive to the store. purchase seeds. grow seeds, harvest lettuce, wheat, etc. Make sandwich.\n" +
            "To be more specific:\n" +
            "\n" +
            "Latency Comparisons\tNanosec\tMicrosec\tMillisec\n" +
            "L1 cache reference\t0.5\t\t\n" +
            "Branch mispredict\t5\t\t\n" +
            "L2 cache reference\t7\t\t\n" +
            "Mutex lock/unlock\t25\t\t\n" +
            "Main memory reference\t100\t\t\n" +
            "Compress 1K bytes with Zippy\t3,000\t3\t\n" +
            "Send 1K bytes over 1 Gbps network\t10,000\t10\t\n" +
            "Read 4K randomly from SSD\t150,000\t150\t\n" +
            "Read 1 MB sequentially from memory\t250,000\t250\t\n" +
            "Round trip within same datacenter\t500,000\t500\t\n" +
            "Read 1 MB sequentially from SSD\t1,000,000\t1,000\t1\n" +
            "Disk seek\t10,000,000\t10,000\t10\n" +
            "Read 1 MB sequentially from disk\t20,000,000\t20,000\t20\n" +
            "Send packet CA->Netherlands->CA\t150,000,000\t150,000\t150";

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

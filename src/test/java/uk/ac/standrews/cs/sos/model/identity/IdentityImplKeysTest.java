package uk.ac.standrews.cs.sos.model.identity;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImplKeysTest extends SetUpTest {

    private SeaConfiguration configuration;
    private final static String BYTES_3K = "c1 c3 c0 86 da 82 92 ea 9a 59 94 a1 fe a5 30 3a \n"+
            "bb 10 13 ec 8c 0e 6d f6 be bb c3 34 dd f3 de 3b \n"+
            "b9 57 b6 6f d8 f8 7d e6 ab 8e ad 7a ac d9 6b 5c \n"+
            "e1 ad eb 59 49 f9 9c 68 97 16 d2 78 4f 97 13 82 \n"+
            "62 07 16 59 0d e0 f8 f1 6d e7 ae fa 43 18 a4 fb \n"+
            "2f d8 90 3d f8 64 0c 5e 4c 6f 57 ac f6 1b ab fd \n"+
            "be 96 30 67 53 af a2 7a 42 97 ca df 1e 8b 58 92 \n"+
            "2e 74 1c 18 1e a1 a8 7b 8b 77 4f 86 b2 fc dd 87 \n"+
            "b5 ae ac 34 9d e5 2f cb e2 75 3c 75 f0 3c 12 56 \n"+
            "eb 13 79 05 69 99 d8 8a e8 c1 ba f6 a8 61 f7 3c \n"+
            "81 d8 35 3c 12 88 1b 93 23 a4 18 bc ea dc b7 be \n"+
            "5f 69 28 52 fa f9 78 36 c1 18 46 2d c2 a9 2d d5 \n"+
            "8c f0 d6 c7 4e d9 42 f2 9f 5a d2 50 bf 09 7c 7d \n"+
            "85 ad 6d 48 15 d6 49 23 76 b5 54 cf e5 71 4e 0b \n"+
            "df 08 4c 17 c7 ca 05 e3 97 96 ef e6 25 5d 67 10 \n"+
            "11 0f 4a 6a b3 cf 56 fb 24 bd 42 5e 0f 94 d4 16 \n"+
            "63 10 2a de 8c 3b 3f 7d 2d e3 82 67 bc 3a f6 61 \n"+
            "1a 38 1d ea de cc ef f8 bb f9 22 55 b9 58 6d de \n"+
            "85 71 39 01 87 a2 b8 bf b3 f7 b8 6e 35 63 bf 59 \n"+
            "b9 d0 77 03 6b 92 0d 29 81 55 e6 c5 16 80 f0 32 \n"+
            "a4 f8 b5 4c 6f 67 a2 72 7e c2 cf d5 12 be 60 ff \n"+
            "b2 b9 74 20 27 ae db c7 08 ce dd ce 69 d2 87 8b \n"+
            "1a ae f9 7c 88 8b e5 d9 26 17 62 64 03 e3 c0 77 \n"+
            "99 ec d0 9e c9 49 b9 dd 39 b2 c2 74 79 a5 67 98 \n"+
            "55 96 c2 f7 f0 f0 b7 02 7c d5 5b e1 96 83 dc 7b \n"+
            "ac af 18 eb 1e c7 2b a5 b6 cc 1d 84 f2 cf 52 b3 \n"+
            "ac ed aa 3d c0 88 26 46 dc 97 d1 cd cf d7 88 81 \n"+
            "e7 df 8c 4c 97 46 b9 44 fc 3b 4c 5d e4 bb 07 21 \n"+
            "bf 13 2b 87 35 01 09 38 c4 dd ea 0c 7e 16 fb 38 \n"+
            "c4 c4 92 18 2e a7 83 a5 fa 7b d1 6c f3 b1 c9 29 \n"+
            "80 3d 6f 88 96 df 33 c8 20 93 b3 59 81 05 71 86 \n"+
            "4f a4 e4 97 a1 3f b3 04 5e e8 81 db 26 78 49 82 \n"+
            "df 57 d2 96 49 88 c8 39 6e 11 a1 ca 08 37 82 67 \n"+
            "69 46 19 2f 2e 4a 68 16 57 7d 23 bb 3e 52 02 9e \n"+
            "f2 72 cf 6c ca fa 19 cd c2 91 b2 40 5b 8e 1b e7 \n"+
            "b8 ed b6 3a 20 8b 5e 51 b6 26 1e b8 71 15 f7 e7 \n"+
            "2f c1 14 ee 3a 41 77 28 66 19 a0 bb f1 7b ce 63 \n"+
            "2a ea 89 fb 5b c7 7a e2 ec 78 ff af 0a 8c 3b 16 \n"+
            "c9 f7 24 b6 79 47 43 fc 91 35 e2 06 ef ee 52 39 \n"+
            "a2 8e 1b c7 32 af 3d 9c 19 38 ee b4 a3 8b da 5b \n"+
            "37 67 23 ae 64 9a 6b d8 c4 b4 88 35 cc 46 c5 c4 \n"+
            "d3 2e 00 40 49 83 03 f5 23 26 35 17 db 90 4f 8d \n"+
            "63 ba 32 e9 01 82 80 f5 34 aa c8 64 81 8e b7 e8 \n"+
            "e4 f2 a6 31 2c 8d dd 19 ac ba e3 f5 1b ab 3b a3 \n"+
            "45 14 c4 f4 a9 ba 24 b3 d0 9b d1 bc df c2 bc 9e \n"+
            "0c 73 16 66 ac 6b 38 ad ea df 4f fe 0b e9 d0 c8 \n"+
            "e9 da 92 fc a8 cb ad c8 e7 93 1d bc d3 c7 11 2b \n"+
            "2e f7 90 d6 b8 fa 22 1f 6e c5 d8 74 fc 5f 74 c7 \n"+
            "fd 34 57 0f ba 07 74 42 49 44 2a 65 02 a0 ce be \n"+
            "59 8e 53 d6 f2 ee 74 d3 c7 fc 44 c3 5b 41 11 64 \n"+
            "28 cb 9a 7d 74 b3 5d 36 fe e7 28 60 3f 24 73 e1 \n"+
            "37 3b af 09 fe 90 20 83 34 77 c5 1d f5 ac 47 34 \n"+
            "f9 a2 43 d1 f6 c9 f0 5c a9 bd cd 5b 22 1a 3b 56 \n"+
            "2f 89 c1 be 86 59 f2 2c 46 60 12 af 0f 36 1d 39 \n"+
            "b1 81 0d 92 25 c7 e7 47 12 41 cc a9 e7 ce 6e ad \n"+
            "c9 f5 bf 3c f3 0c 03 25 69 b0 7d af 92 33 31 6a \n"+
            "d4 3d 36 5e ee 92 3c 39 00 72 ce ea 3d 5c 2f a0 \n"+
            "32 c0 6b 50 14 62 90 01 e1 1a 69 05 ea 55 8e 52 \n"+
            "ac 95 16 da d4 e3 6b da ed e4 17 5e 0a 63 79 84 \n"+
            "5f 6f 8d 91 6b 8a 9e e4 f1 77 68 67 4a ce 47 6b \n"+
            "6c 20 ef 47 13 8b 4e 26 e3 32 99 a6 7f 27 18 9b \n"+
            "b8 9d b6 c9 70 bb 86 4c a4 59 f6 6e f3 a8 f1 05 \n"+
            "1a 48 7f ab 37 87 56 e9 c5 c0 1d c7 d6 72 ff bb \n"+
            "03 31 41 d0 4f 6a fa 26 07 01 88 6e 23 52 77 60 \n";

    @BeforeMethod
    public void setup() throws IOException, SeaConfigurationException {
        SeaConfiguration.setRootName("test");
        configuration = SeaConfiguration.getInstance();

        // Delete any left over keys from past
        deleteKeys(configuration);
    }

    @AfterMethod
    public void tearDown() {
        deleteKeys(configuration);
    }

    @Test
    public void testPublicKeyExists() throws KeyGenerationException, KeyLoadedException {
        Identity identity = new IdentityImpl(configuration);
        assertNotNull(identity.getPublicKey());
    }

    @Test
    public void testPublicKeyLoadedExists() throws KeyGenerationException, KeyLoadedException {
        Identity identity = new IdentityImpl(configuration);
        assertNotNull(identity.getPublicKey());

        identity = new IdentityImpl(configuration);
        assertNotNull(identity.getPublicKey());
    }

    @Test
    public void testEncryptDecrypt() throws EncryptionException, DecryptionException, KeyGenerationException, KeyLoadedException {
        Identity identity = new IdentityImpl(configuration);

        byte[] signature = identity.sign("hello");
        assertTrue(identity.verify("hello", signature));
    }

    @Test
    public void testEncryptDecryptLongData() throws EncryptionException, DecryptionException, KeyGenerationException, KeyLoadedException {
        Identity identity = new IdentityImpl(configuration);

        byte[] signature = identity.sign(BYTES_3K);
        assertTrue(identity.verify(BYTES_3K, signature));
    }

    @Test
    public void testLoadedKeyEncryptDecrypt() throws EncryptionException, DecryptionException, KeyGenerationException, KeyLoadedException {
        Identity identity = new IdentityImpl(configuration);
        Identity identityLoaded = new IdentityImpl(configuration);

        byte[] signature = identity.sign("hello");
        assertTrue(identityLoaded.verify("hello", signature));
    }

    private void deleteKeys(SeaConfiguration configuration) {
        File privKey = new File(configuration.getIdentityPaths()[0]);
        File pubkey = new File(configuration.getIdentityPaths()[1]);

        privKey.delete();
        pubkey.delete();
    }

}
package uk.ac.standrews.cs.sos.impl.roles;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.security.PublicKey;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UserImplTest {

    @Test
    public void constructorTest() throws SignatureException {

        User user = new UserImpl("TEST");

        assertEquals(user.getName(), "TEST");
        assertNotNull(user.guid());
        assertNotNull(user.getSignatureCertificate());
    }

    @Test
    public void constructorWithGUIDTest() throws SignatureException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        User user = new UserImpl(guid,"TEST");

        assertEquals(user.getName(), "TEST");
        assertEquals(user.guid(), guid);
        assertNotNull(user.getSignatureCertificate());
    }

    @Test
    public void constructorWithGUIDAndSignatureTest() throws SignatureException, CryptoException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        PublicKey certificate = DigitalSignature.generateKeys().getPublic();
        User user = new UserImpl(guid,"TEST", certificate);

        assertEquals(user.getName(), "TEST");
        assertEquals(user.guid(), guid);
        assertEquals(user.getSignatureCertificate(), certificate);
    }

    @Test
    public void signAndVerify() throws SignatureException {

        User user = new UserImpl("TEST");

        String signedText = user.sign("EXAMPLE_TEXT");
        boolean verified = user.verify("EXAMPLE_TEXT", signedText);
        assertTrue(verified);
    }
}
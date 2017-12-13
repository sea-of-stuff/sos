package uk.ac.standrews.cs.sos.impl.protocol;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.security.PublicKey;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
// About the PowerMockIgnore for SSL:
// This is a known issue between PowerMock and the SSL context.
// Solution found here: http://mathieuhicauber-java.blogspot.co.uk/2013/07/powermock-and-ssl-context.html
// For future reference: use @PowerMockIgnore({"javax.net.","javax.security."})
// when using Apache HttpClient
@PrepareForTest(DigitalSignature.class)
public class ProtocolTest extends SetUpTest {

    protected PublicKey mockSignatureCertificate;
//    @ObjectFactory
//    public IObjectFactory getObjectFactory() {
//        return new org.powermock.modules.testng.PowerMockObjectFactory();
//    }

    @BeforeMethod
    public void setUp() throws GUIDGenerationException, ConfigurationException, CryptoException, IOException, SOSException {

        try {
            mockSignatureCertificate = mock(PublicKey.class);
            PowerMockito.mockStatic(DigitalSignature.class);
            PowerMockito.when(DigitalSignature.verify64(any(PublicKey.class), any(String.class), any(String.class))).thenReturn(true);
            PowerMockito.when(DigitalSignature.getCertificateString(any(PublicKey.class))).thenReturn("CERTIFICATE_MOCK_TEST");
        } catch (CryptoException e) {
            throw new SOSProtocolException("Protocol Mocking errors");
        }
    }
}

/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

    @BeforeMethod
    public void setUp() throws GUIDGenerationException, ConfigurationException, IOException, SOSException {

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

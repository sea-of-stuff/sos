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
package uk.ac.standrews.cs.sos.impl.usro;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.security.PublicKey;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UserImplTest {

    @BeforeMethod
    public void setUp() {

        SOSLocalNode.settings = new SettingsConfiguration.Settings();
        SOSLocalNode.settings.setKeys(new SettingsConfiguration.Settings.KeysSettings());
        SOSLocalNode.settings.getKeys().setLocation(System.getProperty("user.home") + "/sos/keys/");
    }

    @Test
    public void constructorTest() throws SignatureException {

        User user = new UserImpl("TEST");

        assertEquals(user.getName(), "TEST");
        assertNotNull(user.guid());
        assertNotNull(user.getSignaturePublicKey());
    }

    @Test
    public void constructorWithGUIDTest() throws SignatureException {

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        User user = new UserImpl(guid,"TEST");

        assertEquals(user.getName(), "TEST");
        assertEquals(user.guid(), guid);
        assertNotNull(user.getSignaturePublicKey());
    }

    @Test
    public void constructorWithGUIDAndSignatureTest() throws SignatureException, CryptoException {

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        PublicKey certificate = DigitalSignature.generateKeys().getPublic();
        User user = new UserImpl(guid,"TEST", certificate);

        assertEquals(user.getName(), "TEST");
        assertEquals(user.guid(), guid);
        assertEquals(user.getSignaturePublicKey(), certificate);
    }

    @Test
    public void signAndVerify() throws SignatureException {

        User user = new UserImpl("TEST");

        String signedText = user.sign("EXAMPLE_TEXT");
        boolean verified = user.verify("EXAMPLE_TEXT", signedText);
        assertTrue(verified);
    }
}
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
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RoleImplTest {

    @BeforeMethod
    public void setUp() {

        SOSLocalNode.settings = new SettingsConfiguration.Settings();
        SOSLocalNode.settings.setKeys(new SettingsConfiguration.Settings.KeysSettings());
        SOSLocalNode.settings.getKeys().setLocation(System.getProperty("user.home") + "/sos/keys/");
    }

    @Test
    public void constructorTest() throws SignatureException, ProtectionException {

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        assertEquals(role.getName(), "ROLE_TEST");
        assertNotNull(role.guid());
        assertNotNull(role.getPubKey());
        assertNotNull(role.getSignatureCertificate());
        assertNotNull(role.getSignature());
        assertNotNull(role.getUser());
    }

    @Test
    public void protectTest() throws ProtectionException, SignatureException, CryptoException {

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        SecretKey key = SymmetricEncryption.generateRandomKey();
        String encryptedKey = role.encrypt(key);

        SecretKey decryptedKey = role.decrypt(encryptedKey);
        assertEquals(decryptedKey, key);
    }
}
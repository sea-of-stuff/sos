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
package uk.ac.standrews.cs.sos.impl.json;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.metadata.MetaType;
import uk.ac.standrews.cs.sos.impl.metadata.Property;
import uk.ac.standrews.cs.sos.impl.metadata.SecureMetadataManifest;
import uk.ac.standrews.cs.sos.impl.usro.RoleImpl;
import uk.ac.standrews.cs.sos.impl.usro.UserImpl;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureMetadata;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureMetadataSerializerTest extends SetUpTest {

    @Test
    public void basicSerializationAndDeserialization() throws ProtectionException, SignatureException, ManifestNotMadeException, IOException, CryptoException {

        HashMap<String, Property> metadata = new LinkedHashMap<>();
        metadata.put("String", new Property("String", "image/jpeg"));
        metadata.put("Number", new Property("Number", 1));

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        SecureMetadata secureMetadata = new SecureMetadataManifest(metadata, role);
        assertNotNull(secureMetadata);

        String secureMetadataJSON = secureMetadata.toString();
        SecureMetadata parsedSecureMetadata = JSONHelper.jsonObjMapper().readValue(secureMetadataJSON, SecureMetadata.class);
        assertNotNull(parsedSecureMetadata);

        assertEquals(parsedSecureMetadata.getType(), ManifestType.METADATA_PROTECTED);
        assertEquals(parsedSecureMetadata.getAllPropertyNames().length, 2);

        assertNotNull(parsedSecureMetadata.keysRoles());
        assertEquals(parsedSecureMetadata.keysRoles().size(), 1);
    }

    @Test
    public void basicSerializationAndDeserializationWithEncryptionTested() throws ProtectionException, SignatureException, ManifestNotMadeException, IOException, CryptoException {

        HashMap<String, Property> metadata = new LinkedHashMap<>();
        metadata.put("String", new Property("String", "image/jpeg"));

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        SecureMetadata secureMetadata = new SecureMetadataManifest(metadata, role);
        String secureMetadataJSON = secureMetadata.toString();
        SecureMetadata parsedSecureMetadata = JSONHelper.jsonObjMapper().readValue(secureMetadataJSON, SecureMetadata.class);

        Map.Entry<IGUID, String> keyRole = parsedSecureMetadata.keysRoles().entrySet().iterator().next();
        SecretKey secretKey = role.decrypt(keyRole.getValue());

        Property property = parsedSecureMetadata.getProperty(parsedSecureMetadata.getAllPropertyNames()[0]);
        String clearKey = SymmetricEncryption.decrypt(secretKey, property.getKey());
        String clearValue = SymmetricEncryption.decrypt(secretKey, property.getValue_s());
        assertEquals(property.getType(), MetaType.STRING);
        assertEquals(clearKey, "String");
        assertEquals(clearValue, "image/jpeg");
    }

    @Test
    public void basicSerializationAndDeserializationWithEncryptionOnNumberTested() throws ProtectionException, SignatureException, ManifestNotMadeException, IOException, CryptoException {

        HashMap<String, Property> metadata = new LinkedHashMap<>();
        metadata.put("Number", new Property("Number", 1));

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        SecureMetadata secureMetadata = new SecureMetadataManifest(metadata, role);
        String secureMetadataJSON = secureMetadata.toString();
        SecureMetadata parsedSecureMetadata = JSONHelper.jsonObjMapper().readValue(secureMetadataJSON, SecureMetadata.class);

        Map.Entry<IGUID, String> keyRole = parsedSecureMetadata.keysRoles().entrySet().iterator().next();
        SecretKey secretKey = role.decrypt(keyRole.getValue());

        Property property = parsedSecureMetadata.getProperty(parsedSecureMetadata.getAllPropertyNames()[0]);
        String clearKey = SymmetricEncryption.decrypt(secretKey, property.getKey());
        String clearValue = SymmetricEncryption.decrypt(secretKey, property.getValue_s());
        assertEquals(property.getType(), MetaType.LONG);
        assertEquals(clearKey, "Number");
        assertEquals(clearValue, "1");
        assertEquals(Long.parseLong(clearValue), 1);
    }
}
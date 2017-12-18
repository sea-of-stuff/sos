package uk.ac.standrews.cs.sos.impl.json;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.metadata.MetaProperty;
import uk.ac.standrews.cs.sos.impl.metadata.MetaType;
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

        HashMap<String, MetaProperty> metadata = new LinkedHashMap<>();
        metadata.put("String", new MetaProperty("String", "image/jpeg"));
        metadata.put("Number", new MetaProperty("Number", 1));

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

        HashMap<String, MetaProperty> metadata = new LinkedHashMap<>();
        metadata.put("String", new MetaProperty("String", "image/jpeg"));

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        SecureMetadata secureMetadata = new SecureMetadataManifest(metadata, role);
        String secureMetadataJSON = secureMetadata.toString();
        SecureMetadata parsedSecureMetadata = JSONHelper.jsonObjMapper().readValue(secureMetadataJSON, SecureMetadata.class);

        Map.Entry<IGUID, String> keyRole = parsedSecureMetadata.keysRoles().entrySet().iterator().next();
        SecretKey secretKey = role.decrypt(keyRole.getValue());

        MetaProperty metaProperty = parsedSecureMetadata.getProperty(parsedSecureMetadata.getAllPropertyNames()[0]);
        String clearKey = SymmetricEncryption.decrypt(secretKey, metaProperty.getKey());
        String clearValue = SymmetricEncryption.decrypt(secretKey, metaProperty.getValue_s());
        assertEquals(metaProperty.getMetaType(), MetaType.STRING);
        assertEquals(clearKey, "String");
        assertEquals(clearValue, "image/jpeg");
    }

    @Test
    public void basicSerializationAndDeserializationWithEncryptionOnNumberTested() throws ProtectionException, SignatureException, ManifestNotMadeException, IOException, CryptoException {

        HashMap<String, MetaProperty> metadata = new LinkedHashMap<>();
        metadata.put("Number", new MetaProperty("Number", 1));

        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "ROLE_TEST");

        SecureMetadata secureMetadata = new SecureMetadataManifest(metadata, role);
        String secureMetadataJSON = secureMetadata.toString();
        SecureMetadata parsedSecureMetadata = JSONHelper.jsonObjMapper().readValue(secureMetadataJSON, SecureMetadata.class);

        Map.Entry<IGUID, String> keyRole = parsedSecureMetadata.keysRoles().entrySet().iterator().next();
        SecretKey secretKey = role.decrypt(keyRole.getValue());

        MetaProperty metaProperty = parsedSecureMetadata.getProperty(parsedSecureMetadata.getAllPropertyNames()[0]);
        String clearKey = SymmetricEncryption.decrypt(secretKey, metaProperty.getKey());
        String clearValue = SymmetricEncryption.decrypt(secretKey, metaProperty.getValue_s());
        assertEquals(metaProperty.getMetaType(), MetaType.LONG);
        assertEquals(clearKey, "Number");
        assertEquals(clearValue, "1");
        assertEquals(Long.parseLong(clearValue), 1);
    }
}
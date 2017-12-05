package uk.ac.standrews.cs.sos.impl.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataDeserializer;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataSerializer;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureMetadata;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * TODO - work in progress
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = SecureMetadataDeserializer.class)
@JsonSerialize(using = SecureMetadataSerializer.class)
public class SecureMetadataManifest extends MetadataManifest implements SecureMetadata {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureMetadataManifest(Role signer) {
        super(ManifestType.METADATA_PROTECTED, signer);
    }

    public SecureMetadataManifest(HashMap<String, MetaProperty> metadata, Role signer) throws ManifestNotMadeException {
        this(signer);

        try {
            this.metadata = encryptMetadata(metadata);
        } catch (ProtectionException e) {
            throw new ManifestNotMadeException("Unable to encrypt metadata");
        }

        this.guid = makeGUID();

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign compound manifest properly");
        }
    }

    public SecureMetadataManifest(IGUID guid, HashMap<String, MetaProperty> encryptedMetadata, Role signer, String signature, HashMap<IGUID, String> rolesToKeys) {
        this(signer);

        this.guid = guid;
        this.metadata = encryptedMetadata;
        this.signature = signature;
        this.rolesToKeys = rolesToKeys;
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {
        return rolesToKeys;
    }

    @Override
    public void setKeysRoles(HashMap<IGUID, String> keysRoles) {

        this.rolesToKeys = keysRoles;
    }

    @Override
    public void addKeyRole(IGUID role, String encryptedKey) {

        this.rolesToKeys.put(role, encryptedKey);
    }

    private HashMap<String, MetaProperty> encryptMetadata(HashMap<String, MetaProperty> metadata) throws ProtectionException {

        HashMap<String, MetaProperty> encryptedMetadata = new LinkedHashMap<>();

        try {
            SecretKey key = SymmetricEncryption.generateRandomKey();

            for(String metakey:metadata.keySet()) {
                MetaProperty metaProperty = metadata.get(metakey);
                String value = getValue(metaProperty);

                String encryptedKey = SymmetricEncryption.encrypt(key, metakey);
                String encryptedValue = SymmetricEncryption.encrypt(key, value);

                MetaProperty encryptedMetaProperty = new MetaProperty(metaProperty.getMetaType(), encryptedKey, encryptedValue);
                encryptedMetadata.put(encryptedKey, encryptedMetaProperty);
            }

            String encryptedKey = signer.encrypt(key);
            rolesToKeys.put(signer.guid(), encryptedKey);

        } catch (CryptoException e) {
            throw new ProtectionException(e);
        }

        return encryptedMetadata;
    }

    private String getValue(MetaProperty metaProperty) {

        switch(metaProperty.getMetaType()) {
            case STRING:
                return metaProperty.getValue_s();
            case GUID:
                return metaProperty.getValue_g().toMultiHash();
            case LONG:
                return Long.toString(metaProperty.getValue_l());
        }

        return "INVALID";
    }
}

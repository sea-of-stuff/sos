package uk.ac.standrews.cs.sos.impl.metadata;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureMetadata;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureMetadataManifest extends MetadataManifest implements SecureMetadata {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureMetadataManifest(HashMap<String, MetaProperty> metadata, Role signer) throws ManifestNotMadeException {
        super(ManifestType.METADATA_PROTECTED, signer);

        this.rolesToKeys = new LinkedHashMap<>();

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
        super(ManifestType.METADATA_PROTECTED, signer);

        this.guid = guid;
        this.metadata = encryptedMetadata;
        this.signature = signature;
        this.rolesToKeys = rolesToKeys;
    }

    public SecureMetadataManifest(IGUID guid, HashMap<String, MetaProperty> encryptedMetadata, IGUID signerRef, String signature, HashMap<IGUID, String> rolesToKeys) {
        super(ManifestType.METADATA_PROTECTED, signerRef);

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

            for(Map.Entry<String, MetaProperty> tuple:metadata.entrySet()) {
                MetaProperty metaProperty = tuple.getValue();
                String value = getValue(metaProperty);

                String encryptedKey = SymmetricEncryption.encrypt(key, tuple.getKey());
                String encryptedValue = SymmetricEncryption.encrypt(key, value);

                MetaProperty encryptedMetaProperty = new MetaProperty(metaProperty.getMetaType(), encryptedKey, encryptedValue);
                encryptedMetadata.put(encryptedKey, encryptedMetaProperty);
            }

            String encryptedKey = signer.encrypt(key);
            rolesToKeys.put(signer.guid(), encryptedKey);

        } catch (CryptoException | MetadataException e) {
            throw new ProtectionException(e);
        }

        return encryptedMetadata;
    }

    private String getValue(MetaProperty metaProperty) throws MetadataException {

        switch(metaProperty.getMetaType()) {
            case STRING:
                return metaProperty.getValue_s();
            case GUID:
                return metaProperty.getValue_g().toMultiHash();
            case LONG:
                return Long.toString(metaProperty.getValue_l());
            case DOUBLE:
                return Double.toString(metaProperty.getValue_d());
            case BOOLEAN:
                return Boolean.toString(metaProperty.getValue_b());
        }

        throw new MetadataException("Value type " + metaProperty.getMetaType().toString() + " is unknown");
    }
}

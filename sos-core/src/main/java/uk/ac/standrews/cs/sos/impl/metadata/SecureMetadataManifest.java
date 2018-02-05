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

    public SecureMetadataManifest(HashMap<String, Property> metadata, Role signer) throws ManifestNotMadeException {
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

    public SecureMetadataManifest(IGUID guid, HashMap<String, Property> encryptedMetadata, Role signer, String signature, HashMap<IGUID, String> rolesToKeys) {
        super(ManifestType.METADATA_PROTECTED, signer);

        this.guid = guid;
        this.metadata = encryptedMetadata;
        this.signature = signature;
        this.rolesToKeys = rolesToKeys;
    }

    public SecureMetadataManifest(IGUID guid, HashMap<String, Property> encryptedMetadata, IGUID signerRef, String signature, HashMap<IGUID, String> rolesToKeys) {
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

    private HashMap<String, Property> encryptMetadata(HashMap<String, Property> metadata) throws ProtectionException {

        HashMap<String, Property> encryptedMetadata = new LinkedHashMap<>();

        try {
            SecretKey key = SymmetricEncryption.generateRandomKey();

            for(Map.Entry<String, Property> tuple:metadata.entrySet()) {
                Property property = tuple.getValue();
                String value = getValue(property);

                String encryptedKey = SymmetricEncryption.encrypt(key, tuple.getKey());
                String encryptedValue = SymmetricEncryption.encrypt(key, value);

                Property encryptedProperty = new Property(property.getType(), encryptedKey, encryptedValue);
                encryptedMetadata.put(encryptedKey, encryptedProperty);
            }

            String encryptedKey = signer.encrypt(key);
            rolesToKeys.put(signer.guid(), encryptedKey);

        } catch (CryptoException | MetadataException e) {
            throw new ProtectionException(e);
        }

        return encryptedMetadata;
    }

    private String getValue(Property property) throws MetadataException {

        switch(property.getType()) {
            case STRING:
                return property.getValue_s();
            case GUID:
                return property.getValue_g().toMultiHash();
            case LONG:
                return Long.toString(property.getValue_l());
            case DOUBLE:
                return Double.toString(property.getValue_d());
            case BOOLEAN:
                return Boolean.toString(property.getValue_b());
        }

        throw new MetadataException("Value type " + property.getType().toString() + " is unknown");
    }
}

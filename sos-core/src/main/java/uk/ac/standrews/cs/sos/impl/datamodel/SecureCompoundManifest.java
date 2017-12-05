package uk.ac.standrews.cs.sos.impl.datamodel;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureCompoundManifest extends CompoundManifest implements SecureCompound {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureCompoundManifest(CompoundType type, Set<Content> contents, Role signer) throws ManifestNotMadeException {
        super(ManifestType.COMPOUND_PROTECTED, type, signer);

        this.rolesToKeys = new LinkedHashMap<>();

        try {
            this.contents = encryptContents(contents);
        } catch (ProtectionException e) {
            throw new ManifestNotMadeException("Unable to encrypt contents of Secure Compound Manifest");
        }

        this.guid = makeGUID();

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign compound manifest properly");
        }
    }

    public SecureCompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, Role signer, String signature, HashMap<IGUID, String> rolesToKeys) {
        super(type, contentGUID, contents, signer, signature);

        this.manifestType = ManifestType.COMPOUND_PROTECTED;
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

    private Set<Content> encryptContents(Set<Content> contents) throws ProtectionException {

        Set<Content> encryptedContents = new LinkedHashSet<>();

        try {
            SecretKey key = SymmetricEncryption.generateRandomKey();

            for(Content content:contents) {

                if (content.getLabel() != null) { // labels are optional
                    String encryptedLabel = SymmetricEncryption.encrypt(key, content.getLabel());

                    Content encryptedContent = new ContentImpl(encryptedLabel, content.getGUID());
                    encryptedContents.add(encryptedContent);
                } else {

                    encryptedContents.add(content);
                }
            }

            String encryptedKey = signer.encrypt(key);
            rolesToKeys.put(signer.guid(), encryptedKey);

        } catch (CryptoException e) {
            throw new ProtectionException(e);
        }

        return encryptedContents;
    }
}

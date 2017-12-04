package uk.ac.standrews.cs.sos.impl.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataDeserializer;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataSerializer;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.SecureMetadata;

import java.util.HashMap;

/**
 *
 * TODO - work in progress
 * note: we assume that the properties of this metadata are already encrypted
 * note: this is created only in deserialization
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = SecureMetadataDeserializer.class)
@JsonSerialize(using = SecureMetadataSerializer.class)
public class SecureMetadataManifest extends MetadataManifest implements SecureMetadata {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureMetadataManifest(HashMap<IGUID, String> rolesToKeys) {
        super(ManifestType.METADATA_PROTECTED); // TODO - manifest type
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
}

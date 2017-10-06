package uk.ac.standrews.cs.sos.impl.metadata.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataDeserializer;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataSerializer;
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
public class SecureBasicMetadata extends BasicMetadata implements SecureMetadata {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureBasicMetadata(HashMap<IGUID, String> rolesToKeys) {
        super();
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

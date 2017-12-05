package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.metadata.MetaProperty;
import uk.ac.standrews.cs.sos.impl.metadata.SecureMetadataManifest;
import uk.ac.standrews.cs.sos.model.Role;

import java.io.IOException;
import java.util.HashMap;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureMetadataDeserializer extends MetadataDeserializer {

    @Override
    public SecureMetadataManifest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID guid = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);

            HashMap<String, MetaProperty> metadata = getMetadata(node);
            HashMap<IGUID, String> rolesToKeys = getRolesToKeys(node);

            String signature = getSignature(node);
            IGUID signerRef = getSignerRef(node);
            Role signer = getSigner(signerRef);

            if (signer == null) {
                return new SecureMetadataManifest(guid, metadata, signerRef, signature, rolesToKeys);
            } else {
                return new SecureMetadataManifest(guid, metadata, signer, signature, rolesToKeys);
            }

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate Secure Metadata Manifest");
        }
    }

}

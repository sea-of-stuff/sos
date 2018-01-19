package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.datamodel.VersionManifest;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;

import java.io.IOException;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifestDeserializer extends JsonDeserializer<Version> {

    @Override
    public Version deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID invariant = CommonJson.GetGUID(node, JSONConstants.KEY_INVARIANT);
            IGUID version = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            IGUID content = CommonJson.GetGUID(node, JSONConstants.KEY_CONTENT);
            Set<IGUID> prevs = CommonJson.GetGUIDCollection(node, JSONConstants.KEY_PREVIOUS_GUID);
            IGUID metadata = getMetadata(node);

            String signature = getSignature(node);
            IGUID signerRef = getSignerRef(node);
            Role signer = getSigner(signerRef);

            if (signer == null) {
                return new VersionManifest(invariant, version, content, prevs, metadata, signerRef, signature);
            } else {
                return new VersionManifest(invariant, version, content, prevs, metadata, signer, signature);
            }

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate Version Manifest");
        }

    }

    private IGUID getMetadata(JsonNode node) throws GUIDGenerationException {

        IGUID metadata = new InvalidID();
        if (node.has(JSONConstants.KEY_METADATA_GUID)) {
            metadata = CommonJson.GetGUID(node, JSONConstants.KEY_METADATA_GUID);
        }

        return metadata;

    }

}

package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.manifests.SecureVersionManifest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static uk.ac.standrews.cs.sos.json.CommonJson.getRolesToKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureVersionManifestDeserializer extends JsonDeserializer<SecureVersionManifest> {

    @Override
    public SecureVersionManifest deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID invariant = CommonJson.GetGUID(node, JSONConstants.KEY_INVARIANT);
            IGUID version = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            IGUID content = CommonJson.GetGUID(node, JSONConstants.KEY_CONTENT_GUID);

            String signature = null;
            IGUID signer = null;
            if (node.has(JSONConstants.KEY_SIGNATURE) && node.has(JSONConstants.KEY_SIGNER)) {
                signature = node.get(JSONConstants.KEY_SIGNATURE).textValue();
                signer = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_SIGNER).textValue());
                // FIXME - recreate Role signer
            }

            Set<IGUID> prevs = CommonJson.GetGUIDCollection(node, JSONConstants.KEY_PREVIOUS_GUID);

            IGUID metadata = null;
            if (node.has(JSONConstants.KEY_METADATA_GUID)) {
                metadata = CommonJson.GetGUID(node, JSONConstants.KEY_METADATA_GUID);
            }

            HashMap<IGUID, String> rolesToKeys = getRolesToKeys(node);

            return new SecureVersionManifest(invariant, version, content, prevs, metadata, null, signature, rolesToKeys);

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        } catch (ManifestNotMadeException e) {
            throw new IOException("Unable to recreate Secure Version Manifest");
        }
    }
}

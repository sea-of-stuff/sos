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
import uk.ac.standrews.cs.sos.impl.manifests.ContentImpl;
import uk.ac.standrews.cs.sos.impl.manifests.SecureCompoundManifest;
import uk.ac.standrews.cs.sos.model.CompoundType;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.json.CommonJson.getRolesToKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureCompoundManifestDeserializer extends JsonDeserializer<SecureCompoundManifest> {

    @Override
    public SecureCompoundManifest deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID contentGUID = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);

            String signature = null;
            IGUID signer = null;
            if (node.has(JSONConstants.KEY_SIGNATURE) && node.has(JSONConstants.KEY_SIGNER)) {
                signature = node.get(JSONConstants.KEY_SIGNATURE).textValue();
                signer = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_SIGNER).textValue());

                // FIXME - recreate Role signer using USRO Service?
            }

            String compoundTypeString = node.get(JSONConstants.KEY_COMPOUND_TYPE).textValue();
            CompoundType compoundType = CompoundType.valueOf(compoundTypeString);

            JsonNode contentsNode = node.get(JSONConstants.KEY_CONTENTS);
            Set<Content> contents = new LinkedHashSet<>();
            if (contentsNode.isArray()) {
                for(final JsonNode contentNode:contentsNode) {
                    ContentImpl content = JSONHelper.JsonObjMapper().convertValue(contentNode, ContentImpl.class);
                    contents.add(content);
                }
            }

            HashMap<IGUID, String> rolesToKeys = getRolesToKeys(node);

            return new SecureCompoundManifest(compoundType, contentGUID, contents, null, signature, rolesToKeys);

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        } catch (ManifestNotMadeException e) {
            throw new IOException("Unable to recreate Compound Manifest");
        }
    }
}

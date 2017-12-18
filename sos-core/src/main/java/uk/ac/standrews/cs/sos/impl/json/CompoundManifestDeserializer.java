package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.datamodel.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.model.Compound;
import uk.ac.standrews.cs.sos.model.CompoundType;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestDeserializer extends JsonDeserializer<Compound> {

    @Override
    public Compound deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID contentGUID = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            String compoundTypeString = node.get(JSONConstants.KEY_COMPOUND_TYPE).textValue();
            CompoundType compoundType = CompoundType.valueOf(compoundTypeString);
            Set<Content> contents = getContents(node);

            String signature = getSignature(node);
            IGUID signerRef = getSignerRef(node);
            Role signer = getSigner(signerRef);

            if (signer == null) {
                return new CompoundManifest(compoundType, contentGUID, contents, signerRef, signature);
            } else {
                return new CompoundManifest(compoundType, contentGUID, contents, signer, signature);
            }

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate Compound Manifest");
        }

    }

    protected Set<Content> getContents(JsonNode node) {

        JsonNode contentsNode = node.get(JSONConstants.KEY_CONTENTS);
        Set<Content> contents = new LinkedHashSet<>();
        if (contentsNode.isArray()) {
            for(final JsonNode contentNode:contentsNode) {
                ContentImpl content = JSONHelper.jsonObjMapper().convertValue(contentNode, ContentImpl.class);
                contents.add(content);
            }
        }

        return contents;
    }

}


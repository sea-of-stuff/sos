package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestDeserializer extends JsonDeserializer<CompoundManifest> {

    @Override
    public CompoundManifest deserialize(JsonParser jsonParser,
                                        DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID contentGUID = CommonJson.GetGUID(node, ManifestConstants.KEY_CONTENT_GUID);
            String signature = node.get(ManifestConstants.KEY_SIGNATURE).textValue();

            String compoundTypeString = node.get(ManifestConstants.KEY_COMPOUND_TYPE).textValue();
            CompoundType compoundType = CompoundType.valueOf(compoundTypeString);

            JsonNode contentsNode = node.get(ManifestConstants.KEY_CONTENTS);
            Collection<Content> contents = new ArrayList<>();
            if (contentsNode.isArray()) {
                for(final JsonNode contentNode:contentsNode) {
                    Content content = Helper.JsonObjMapper().convertValue(contentNode, Content.class); // TODO - not sure if this works
                    contents.add(content);
                }
            }

            return new CompoundManifest(compoundType, contentGUID, contents, signature);
        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        } catch (ManifestNotMadeException e) {
            throw new IOException("Unable to recreate Compound Manifest");
        }

    }

}

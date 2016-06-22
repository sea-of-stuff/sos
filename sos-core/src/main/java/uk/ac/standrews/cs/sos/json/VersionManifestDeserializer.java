package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.VersionManifest;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifestDeserializer extends JsonDeserializer<VersionManifest> {

    @Override
    public VersionManifest deserialize(JsonParser jsonParser,
                                       DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID invariant = CommonJson.GetGUID(node, ManifestConstants.KEY_INVARIANT);
            IGUID version = CommonJson.GetGUID(node, ManifestConstants.KEY_VERSION);
            IGUID content = CommonJson.GetGUID(node, ManifestConstants.KEY_CONTENT_GUID);

            String signature = null;
            if (node.has(ManifestConstants.KEY_SIGNATURE)) {
                signature = node.get(ManifestConstants.KEY_SIGNATURE).textValue();
            }

            Collection<IGUID> prevs = CommonJson.GetGUIDCollection(node, ManifestConstants.KEY_PREVIOUS_GUID);
            Collection<IGUID> metadata = CommonJson.GetGUIDCollection(node, ManifestConstants.KEY_METADATA_GUID);

            return new VersionManifest(invariant, version, content, prevs, metadata, signature);
        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        }

    }

}
package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.manifests.ContentImpl;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.io.IOException;

public class ContentDeserializer extends JsonDeserializer<ContentImpl> {

    @Override
    public ContentImpl deserialize(JsonParser jsonParser,
                                   DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        ContentImpl ret;
        IGUID guid;
        try {
            guid = CommonJson.GetGUID(node, ManifestConstants.CONTENT_KEY_GUID);
        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        }

        boolean hasLabel = node.has(ManifestConstants.CONTENT_KEY_LABEL);
        if (hasLabel) {
            String label = node.get(ManifestConstants.CONTENT_KEY_LABEL).textValue();
            ret = new ContentImpl(label, guid);
        } else {
            ret = new ContentImpl(guid);
        }


        return ret;
    }

}

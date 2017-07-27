package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.manifests.ContentImpl;

import java.io.IOException;

public class ContentDeserializer extends JsonDeserializer<ContentImpl> {

    @Override
    public ContentImpl deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        ContentImpl ret;
        IGUID guid;
        try {
            guid = CommonJson.GetGUID(node, JSONConstants.CONTENT_KEY_GUID);
        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        }

        boolean hasLabel = node.has(JSONConstants.CONTENT_KEY_LABEL);
        if (hasLabel) {
            String label = node.get(JSONConstants.CONTENT_KEY_LABEL).textValue();
            ret = new ContentImpl(label, guid);
        } else {
            ret = new ContentImpl(guid);
        }


        return ret;
    }

}

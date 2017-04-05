package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.ManifestConstants;
import uk.ac.standrews.cs.sos.impl.metadata.basic.BasicMetadata;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataDeserializer extends JsonDeserializer<BasicMetadata> {

    @Override
    public BasicMetadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        BasicMetadata basicMetadata = new BasicMetadata();
        try {
            // TODO - should not trust this GUID. Better to recreate it?
            basicMetadata.setGUID(GUIDFactory.recreateGUID(node.get(ManifestConstants.KEY_GUID).asText()));
        } catch (GUIDGenerationException e) {
            throw new IOException(e);
        }

        JsonNode properties = node.get(ManifestConstants.KEY_META_PROPERTIES);
        Iterator<JsonNode> it = properties.elements();
        while(it.hasNext()) {
            JsonNode n = it.next();

            String key = n.get(ManifestConstants.KEY_META_KEY).asText();
            String type = n.get(ManifestConstants.KEY_META_TYPE).asText();
            Object value = getObject(n.get(ManifestConstants.KEY_META_VALUE), type);

            basicMetadata.addProperty(key, value);
        }

        return basicMetadata;
    }

    private Object getObject(JsonNode element, String type) {

        switch(type) {
            case "INT":
                return element.asInt();
            case "STRING":
                return element.asText();
        }

        return null;
    }
}

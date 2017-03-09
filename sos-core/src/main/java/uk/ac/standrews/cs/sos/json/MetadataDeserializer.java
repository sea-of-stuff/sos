package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.metadata.basic.BasicMetadata;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataDeserializer extends JsonDeserializer<BasicMetadata> {

    @Override
    public BasicMetadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        BasicMetadata basicMetadata = new BasicMetadata();

        JsonNode properties = node.get(ManifestConstants.KEY_META_PROPERTIES);

        Iterator<JsonNode> it = properties.elements();
        while(it.hasNext()) {
            JsonNode n = it.next();

            Iterator<Map.Entry<String, JsonNode>> property = n.fields();
            if (property.hasNext()) {
                Map.Entry<String, JsonNode> p = property.next();
                basicMetadata.addProperty(p.getKey(), p.getValue().getNodeType() == JsonNodeType.NUMBER ?
                                p.getValue().asInt() : p.getValue().asText());
            }
        }

        try {
            basicMetadata.build();
        } catch (GUIDGenerationException e) {
            throw new IOException(e);
        }

        return basicMetadata;
    }
}

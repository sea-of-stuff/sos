package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
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
        JsonNode properties = node.get("Properties");
        if (properties.isArray()) {

            Iterator<Map.Entry<String, JsonNode>> it = properties.fields();
            while(it.hasNext()) {
                Map.Entry<String, JsonNode> element = it.next();
                String property = element.getKey();
                String value = element.getValue().toString();
                basicMetadata.addProperty(property, value);
            }

        }

        return basicMetadata;
    }
}

package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.model.metadata.basic.BasicMetadata;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataDeserializer extends JsonDeserializer<BasicMetadata> {

    @Override
    public BasicMetadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        BasicMetadata basicMetadata = new BasicMetadata();
        JsonNode properties = node.withArray("Properties");

        for(JsonNode property:properties) {
            String key = property.get("Key").asText();
            String value = property.get("Value").asText();

            basicMetadata.addProperty(key, value);
        }

        return basicMetadata;
    }
}
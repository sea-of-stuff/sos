package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import uk.ac.standrews.cs.sos.model.metadata.basic.BasicMetadata;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataDeserializer extends JsonDeserializer<BasicMetadata> {

    @Override
    public BasicMetadata deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        return null;
    }
}

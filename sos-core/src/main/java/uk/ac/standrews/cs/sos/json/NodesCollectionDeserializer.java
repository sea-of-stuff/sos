package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionDeserializer extends JsonDeserializer<NodesCollection> {

    @Override
    public NodesCollection deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        return null;
    }
}

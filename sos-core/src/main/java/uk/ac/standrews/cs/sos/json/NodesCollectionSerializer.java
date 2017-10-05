package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionSerializer extends JsonSerializer<NodesCollection> {

    @Override
    public void serialize(NodesCollection nodesCollection, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

    }
}

package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.io.IOException;

import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_NODES_COLLECTION_REFS;
import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_NODES_COLLECTION_TYPE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionSerializer extends JsonSerializer<NodesCollection> {

    @Override
    public void serialize(NodesCollection nodesCollection, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(KEY_NODES_COLLECTION_TYPE, nodesCollection.type().toString());

        jsonGenerator.writeFieldName(KEY_NODES_COLLECTION_REFS);
        jsonGenerator.writeStartArray();
        if (nodesCollection.nodesRefs() != null) {
            for (IGUID guid : nodesCollection.nodesRefs()) {
                jsonGenerator.writeString(guid.toMultiHash());
            }
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}

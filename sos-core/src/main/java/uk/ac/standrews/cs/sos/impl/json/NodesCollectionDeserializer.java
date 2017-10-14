package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionDeserializer extends JsonDeserializer<NodesCollection> {

    @Override
    public NodesCollection deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            NodesCollectionType type = NodesCollectionType.get(node.get(JSONConstants.KEY_NODES_COLLECTION_TYPE).asText());

            Set<IGUID> nodes = new LinkedHashSet<>();
            JsonNode nodes_n = node.get(JSONConstants.KEY_NODES_COLLECTION_REFS);
            for (JsonNode node_n : nodes_n) {
                IGUID ref = GUIDFactory.recreateGUID(node_n.asText());
                nodes.add(ref);
            }

            if (type != NodesCollectionType.SPECIFIED) {
                return new NodesCollectionImpl(type);
            } else {
                return new NodesCollectionImpl(nodes);
            }

        } catch (GUIDGenerationException | NodesCollectionException e) {

            throw new IOException("Unable to deserialize a NodesCollection");
        }

    }

}

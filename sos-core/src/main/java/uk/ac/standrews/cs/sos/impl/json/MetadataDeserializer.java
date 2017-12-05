package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.metadata.MetaProperty;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataManifest;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataDeserializer extends JsonDeserializer<Metadata> {

    @Override
    public Metadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        IGUID guid;
        try {
            String guidS = node.get(JSONConstants.KEY_GUID).asText();
            guid = GUIDFactory.recreateGUID(guidS);
        } catch (GUIDGenerationException e) {
            throw new IOException(e);
        }

        HashMap<String, MetaProperty> metadata = new HashMap<>();

        JsonNode properties = node.get(JSONConstants.KEY_META_PROPERTIES);
        Iterator<JsonNode> it = properties.elements();
        while(it.hasNext()) {
            JsonNode n = it.next();

            String key = n.get(JSONConstants.KEY_META_KEY).asText();
            String type = n.get(JSONConstants.KEY_META_TYPE).asText();

            MetaProperty metaProperty= getObject(n.get(JSONConstants.KEY_META_VALUE), type, key);
            metadata.put(key, metaProperty);
        }

        return new MetadataManifest(guid, metadata, null, "SIGNATURE!");
    }

    protected MetaProperty getObject(JsonNode element, String type, String key) {

        switch(type.toUpperCase()) {
            case "LONG":
                return new MetaProperty(key, element.asLong());
            case "STRING":
                return new MetaProperty(key, element.asText());
            case "GUID":
                try {
                    return new MetaProperty(key, GUIDFactory.recreateGUID(element.asText()));
                } catch (GUIDGenerationException e) {
                    return new MetaProperty(key, new InvalidID());
                }
        }

        return null;
    }
}

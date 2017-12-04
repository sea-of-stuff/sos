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
import uk.ac.standrews.cs.sos.impl.metadata.SecureMetadataManifest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.getRolesToKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureMetadataDeserializer extends JsonDeserializer<SecureMetadataManifest> {

    @Override
    public SecureMetadataManifest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        HashMap<IGUID, String> rolesToKeys;
        try {
            rolesToKeys = getRolesToKeys(node);
        } catch (GUIDGenerationException e) {
            throw new IOException();
        }

        SecureMetadataManifest basicMetadata = new SecureMetadataManifest(rolesToKeys);
        try {
            String guidS = node.get(JSONConstants.KEY_GUID).asText();
            GUIDFactory.recreateGUID(guidS);
            // FIXME basicMetadata.setGUID(GUIDFactory.recreateGUID(guidS));
        } catch (GUIDGenerationException e) {
            throw new IOException(e);
        }

        JsonNode properties = node.get(JSONConstants.KEY_META_PROPERTIES);
        Iterator<JsonNode> it = properties.elements();
        while(it.hasNext()) {
            JsonNode n = it.next();

            String key = n.get(JSONConstants.KEY_META_KEY).asText();
            String type = n.get(JSONConstants.KEY_META_TYPE).asText();
            Object value = getObject(n.get(JSONConstants.KEY_META_VALUE), type);

            // FIXME - basicMetadata.addProperty(key, value);
            // reuse code from other deserializer if possible
        }

        return basicMetadata;
    }

    private Object getObject(JsonNode element, String type) {

        switch(type.toUpperCase()) {
            case "LONG":
                return element.asLong();
            case "INT":
                return element.asInt();
            case "STRING":
                return element.asText();
            case "GUID":
                try {
                    return GUIDFactory.recreateGUID(element.asText());
                } catch (GUIDGenerationException e) {
                    return new InvalidID();
                }
        }

        return null;
    }
}

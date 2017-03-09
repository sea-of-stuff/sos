package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.interfaces.model.Metadata;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataSerializer extends JsonSerializer<Metadata> {

    @Override
    public void serialize(Metadata metadata, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(ManifestConstants.KEY_GUID, metadata.guid().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_TYPE, ManifestType.METADATA.toString());

        jsonGenerator.writeFieldName(ManifestConstants.KEY_META_PROPERTIES);
        jsonGenerator.writeStartArray();
        serializeElements(metadata, jsonGenerator);
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void serializeElements(Metadata metadata, JsonGenerator jsonGenerator) throws IOException {
        String[] properties = metadata.getAllPropertyNames();
        for(String property:properties) {
            jsonGenerator.writeStartObject();

            Object value = metadata.getProperty(property);
            if (value instanceof Integer) {
                jsonGenerator.writeNumberField(property, (Integer) value);
            } else {
                jsonGenerator.writeStringField(property, (String) value);
            }

            jsonGenerator.writeEndObject();

        }
    }
}

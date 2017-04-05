package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.ManifestConstants;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;

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
            String type = getType(value);

            jsonGenerator.writeStringField(ManifestConstants.KEY_META_KEY, property);
            jsonGenerator.writeStringField(ManifestConstants.KEY_META_TYPE, type);
            writeValue(jsonGenerator, type, value);

            jsonGenerator.writeEndObject();

        }
    }

    // FIXME - type could also be REF/GUID
    public String getType(Object value) {
        if (value instanceof Integer) {
            return "INT";
        } else {
            return "STRING";
        }
    }

    private void writeValue(JsonGenerator jsonGenerator, String type, Object value) throws IOException {
        switch(type) {
            case "INT":
                jsonGenerator.writeNumberField(ManifestConstants.KEY_META_VALUE, (Integer) value);
                break;
            case "STRING":
                jsonGenerator.writeStringField(ManifestConstants.KEY_META_VALUE, (String) value);
                break;
        }
    }

}

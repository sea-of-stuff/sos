package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.metadata.Property;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataSerializer extends JsonSerializer<Metadata> {

    @Override
    public void serialize(Metadata metadata, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, metadata.guid().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, metadata.getType().toString());

        jsonGenerator.writeFieldName(JSONConstants.KEY_META_PROPERTIES);
        jsonGenerator.writeStartArray();
        serializeElements(metadata, jsonGenerator);
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    void serializeElements(Metadata metadata, JsonGenerator jsonGenerator) throws IOException {

        String[] properties = metadata.getAllPropertyNames();
        for(String property:properties) {
            jsonGenerator.writeStartObject();

            Property metaProperty = metadata.getProperty(property);

            jsonGenerator.writeStringField(JSONConstants.KEY_META_KEY, metaProperty.getKey());
            jsonGenerator.writeStringField(JSONConstants.KEY_META_TYPE, metaProperty.getType().toString());
            writeValue(jsonGenerator, metaProperty);

            jsonGenerator.writeEndObject();
        }
    }

    private void writeValue(JsonGenerator jsonGenerator, Property property) throws IOException {

        if (property.isEncrypted()) {
            jsonGenerator.writeStringField(JSONConstants.KEY_META_VALUE, property.getValue_s());

        } else {

            switch (property.getType()) {
                case ANY:
                    jsonGenerator.writeNullField(JSONConstants.KEY_META_VALUE);
                    break;
                case LONG:
                    jsonGenerator.writeNumberField(JSONConstants.KEY_META_VALUE, property.getValue_l());
                    break;
                case DOUBLE:
                    jsonGenerator.writeNumberField(JSONConstants.KEY_META_VALUE, property.getValue_d());
                    break;
                case BOOLEAN:
                    jsonGenerator.writeBooleanField(JSONConstants.KEY_META_VALUE, property.getValue_b());
                    break;
                case STRING:
                    jsonGenerator.writeStringField(JSONConstants.KEY_META_VALUE, property.getValue_s());
                    break;
                case GUID:
                    jsonGenerator.writeStringField(JSONConstants.KEY_META_VALUE, property.getValue_g().toMultiHash());
                    break;
            }
        }
    }

}

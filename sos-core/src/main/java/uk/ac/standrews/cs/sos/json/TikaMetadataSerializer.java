package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadata;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataSerializer extends JsonSerializer<TikaMetadata> {

    @Override
    public void serialize(TikaMetadata metadata, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        // TODO - avoid magic constants
        try {
            jsonGenerator.writeStringField("GUID", metadata.guid().toString());

            jsonGenerator.writeFieldName("Properties");
            jsonGenerator.writeStartArray();
            serializeElements(metadata, jsonGenerator);
            jsonGenerator.writeEndArray();

        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }

        jsonGenerator.writeEndObject();
    }

    private void serializeElements(TikaMetadata metadata, JsonGenerator jsonGenerator) throws IOException {
        String[] properties = metadata.getAllPropertyNames();
        for(String property:properties) {
            String value = metadata.getProperty(property);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(property, value);
            jsonGenerator.writeEndObject();
        }
    }
}

package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataSerializer extends JsonSerializer<SOSMetadata> {

    @Override
    public void serialize(SOSMetadata metadata, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(ManifestConstants.KEY_GUID, metadata.guid().toString());

        jsonGenerator.writeFieldName(ManifestConstants.KEY_META_PROPERTIES);
        jsonGenerator.writeStartArray();
        serializeElements(metadata, jsonGenerator);
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void serializeElements(SOSMetadata metadata, JsonGenerator jsonGenerator) throws IOException {
        String[] properties = metadata.getAllPropertyNames();
        for(String property:properties) {
            String value = metadata.getProperty(property);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(property, value);
            jsonGenerator.writeEndObject();

        }
    }
}

package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContentSerializer extends JsonSerializer<Content> {

    @Override
    public void serialize(Content content, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject();

        String label = content.getLabel();
        IGUID guid = content.getGUID();

        if (label != null && !label.isEmpty()) {
            jsonGenerator.writeStringField(ManifestConstants.CONTENT_KEY_LABEL, label);
        }

        jsonGenerator.writeStringField(ManifestConstants.CONTENT_KEY_GUID, guid.toString());

        jsonGenerator.writeEndObject();
    }
}

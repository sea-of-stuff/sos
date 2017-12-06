package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.Version;

import java.io.IOException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifestSerializer extends JsonSerializer<Version> {

    @Override
    public void serialize(Version versionManifest, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, versionManifest.getType().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, versionManifest.version().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_INVARIANT, versionManifest.invariant().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_CONTENT_GUID, versionManifest.content().toMultiHash());

        if (versionManifest.getMetadata() != null) {
            jsonGenerator.writeStringField(JSONConstants.KEY_METADATA_GUID, versionManifest.getMetadata().toMultiHash());
        }

        if (versionManifest.previous() != null) {
            jsonGenerator.writeFieldName(JSONConstants.KEY_PREVIOUS_GUID);
            jsonGenerator.writeStartArray();
            serializePrevious(versionManifest, jsonGenerator);
            jsonGenerator.writeEndArray();
        }

        String signature = versionManifest.getSignature();
        IGUID signer = versionManifest.getSigner();
        if (signature != null && !signature.isEmpty() && signer != null && !signer.isInvalid()) {
            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNER, signer.toMultiHash());
            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNATURE, signature);
        }

        jsonGenerator.writeEndObject();
    }

    private void serializePrevious(Version versionManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<IGUID> previous = versionManifest.previous();
        for(IGUID prev:previous) {
            jsonGenerator.writeString(prev.toMultiHash());
        }
    }

}

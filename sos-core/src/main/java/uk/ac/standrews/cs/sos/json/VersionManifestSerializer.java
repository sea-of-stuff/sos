package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.VersionManifest;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifestSerializer extends JsonSerializer<VersionManifest> {

    @Override
    public void serialize(VersionManifest versionManifest,
                          JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(ManifestConstants.KEY_TYPE, versionManifest.getManifestType().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_CONTENT_GUID, versionManifest.getContentGUID().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_INVARIANT, versionManifest.getInvariantGUID().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_VERSION, versionManifest.getVersionGUID().toString());

        if (versionManifest.getPreviousVersions() != null) {
            jsonGenerator.writeFieldName(ManifestConstants.KEY_PREVIOUS_GUID);
            jsonGenerator.writeStartArray();
            serializePrevious(versionManifest, jsonGenerator);
            jsonGenerator.writeEndArray();
        }

        if (versionManifest.getMetadata() != null) {
            jsonGenerator.writeFieldName(ManifestConstants.KEY_METADATA_GUID);
            jsonGenerator.writeStartArray();
            serializeMetadata(versionManifest, jsonGenerator);
            jsonGenerator.writeEndArray();
        }

        String signature = versionManifest.getSignature();
        if (signature != null && !signature.isEmpty()) {
            jsonGenerator.writeStringField(ManifestConstants.KEY_SIGNATURE, signature);
        }

        jsonGenerator.writeEndObject();
    }

    private void serializePrevious(VersionManifest versionManifest, JsonGenerator jsonGenerator) throws IOException {
        Collection<IGUID> previous = versionManifest.getPreviousVersions();
        for(IGUID prev:previous) {
            jsonGenerator.writeString(prev.toString());
        }
    }

    private void serializeMetadata(VersionManifest versionManifest, JsonGenerator jsonGenerator) throws IOException {
        Collection<IGUID> metadata = versionManifest.getMetadata();
        for(IGUID meta:metadata) {
            jsonGenerator.writeString(meta.toString());
        }
    }
}

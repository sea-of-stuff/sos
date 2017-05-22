package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.constants.ManifestConstants;
import uk.ac.standrews.cs.sos.impl.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;

import java.io.IOException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifestSerializer extends JsonSerializer<VersionManifest> {

    @Override
    public void serialize(VersionManifest versionManifest, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(ManifestConstants.KEY_TYPE, ManifestType.VERSION.toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_GUID, versionManifest.getVersionGUID().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_INVARIANT, versionManifest.getInvariantGUID().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_CONTENT_GUID, versionManifest.getContentGUID().toString());

        if (versionManifest.getMetadata() != null) {
            jsonGenerator.writeStringField(ManifestConstants.KEY_METADATA_GUID, versionManifest.getMetadata().toString());
        }

        if (versionManifest.getPreviousVersions() != null) {
            jsonGenerator.writeFieldName(ManifestConstants.KEY_PREVIOUS_GUID);
            jsonGenerator.writeStartArray();
            serializePrevious(versionManifest, jsonGenerator);
            jsonGenerator.writeEndArray();
        }

        String signature = versionManifest.getSignature();
        IGUID signer = versionManifest.getSigner();
        if (signature != null && !signature.isEmpty() && signer != null && !signer.isInvalid()) {
            jsonGenerator.writeStringField(ManifestConstants.KEY_SIGNER, signer.toString());
            jsonGenerator.writeStringField(ManifestConstants.KEY_SIGNATURE, signature);
        }

        jsonGenerator.writeEndObject();
    }

    private void serializePrevious(VersionManifest versionManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<IGUID> previous = versionManifest.getPreviousVersions();
        for(IGUID prev:previous) {
            jsonGenerator.writeString(prev.toString());
        }
    }

}

package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.manifests.SecureVersionManifest;
import uk.ac.standrews.cs.sos.impl.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;

import java.io.IOException;
import java.util.Set;

import static uk.ac.standrews.cs.sos.json.CommonJson.serializeKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureVersionManifestSerializer extends JsonSerializer<SecureVersionManifest> {

    @Override
    public void serialize(SecureVersionManifest versionManifest, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, ManifestType.VERSION_PROTECTED.toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, versionManifest.getVersionGUID().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_INVARIANT, versionManifest.getInvariantGUID().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_CONTENT_GUID, versionManifest.getContentGUID().toMultiHash());

        if (versionManifest.getMetadata() != null) {
            jsonGenerator.writeStringField(JSONConstants.KEY_METADATA_GUID, versionManifest.getMetadata().toMultiHash());
        }

        if (versionManifest.getPreviousVersions() != null) {
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

        serializeKeys(versionManifest, jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    private void serializePrevious(VersionManifest versionManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<IGUID> previous = versionManifest.getPreviousVersions();
        for (IGUID prev : previous) {
            jsonGenerator.writeString(prev.toMultiHash());
        }
    }

}

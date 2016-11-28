package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;

import java.io.IOException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifestSerializer extends JsonSerializer<AssetManifest> {

    @Override
    public void serialize(AssetManifest assetManifest,
                          JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(ManifestConstants.KEY_TYPE, ManifestType.ASSET.toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_CONTENT_GUID, assetManifest.getContentGUID().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_INVARIANT, assetManifest.getInvariantGUID().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_VERSION, assetManifest.getVersionGUID().toString());

        if (assetManifest.getPreviousVersions() != null) {
            jsonGenerator.writeFieldName(ManifestConstants.KEY_PREVIOUS_GUID);
            jsonGenerator.writeStartArray();
            serializePrevious(assetManifest, jsonGenerator);
            jsonGenerator.writeEndArray();
        }

        if (assetManifest.getMetadata() != null) {
            jsonGenerator.writeFieldName(ManifestConstants.KEY_METADATA_GUID);
            jsonGenerator.writeStartArray();
            serializeMetadata(assetManifest, jsonGenerator);
            jsonGenerator.writeEndArray();
        }

        String signature = assetManifest.getSignature();
        if (signature != null && !signature.isEmpty()) {
            jsonGenerator.writeStringField(ManifestConstants.KEY_SIGNATURE, signature);
        }

        jsonGenerator.writeEndObject();
    }

    private void serializePrevious(AssetManifest assetManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<IGUID> previous = assetManifest.getPreviousVersions();
        for(IGUID prev:previous) {
            jsonGenerator.writeString(prev.toString());
        }
    }

    private void serializeMetadata(AssetManifest assetManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<IGUID> metadata = assetManifest.getMetadata();
        for(IGUID meta:metadata) {
            jsonGenerator.writeString(meta.toString());
        }
    }
}

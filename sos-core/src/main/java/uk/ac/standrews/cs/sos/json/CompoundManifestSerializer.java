package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestSerializer extends JsonSerializer<CompoundManifest> {

    @Override
    public void serialize(CompoundManifest compoundManifest,
                          JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(ManifestConstants.KEY_TYPE, ManifestType.COMPOUND.toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_COMPOUND_TYPE, compoundManifest.getType().toString());
        jsonGenerator.writeStringField(ManifestConstants.KEY_CONTENT_GUID, compoundManifest.getContentGUID().toString());

        jsonGenerator.writeFieldName(ManifestConstants.KEY_CONTENTS);
        jsonGenerator.writeStartArray();
        serializeContents(compoundManifest, jsonGenerator);
        jsonGenerator.writeEndArray();

        String signature = compoundManifest.getSignature();
        if (signature != null && !signature.isEmpty()) {
            jsonGenerator.writeStringField(ManifestConstants.KEY_SIGNATURE, signature);
        }

        jsonGenerator.writeEndObject();
    }

    private void serializeContents(CompoundManifest compoundManifest, JsonGenerator jsonGenerator) throws IOException {
        Collection<Content> contents = compoundManifest.getContents();
        for(Content content:contents) {
            jsonGenerator.writeObject(content);
        }
    }
}

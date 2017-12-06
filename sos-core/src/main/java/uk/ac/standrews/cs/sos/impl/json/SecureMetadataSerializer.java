package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.SecureMetadata;

import java.io.IOException;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.serializeKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureMetadataSerializer extends MetadataSerializer {

    @Override
    public void serialize(Metadata metadata, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, metadata.guid().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, metadata.getType().toString());

        jsonGenerator.writeFieldName(JSONConstants.KEY_META_PROPERTIES);
        jsonGenerator.writeStartArray();
        serializeElements(metadata, jsonGenerator);
        jsonGenerator.writeEndArray();

        String signature = metadata.getSignature();
        IGUID signer = metadata.getSigner();
        if (signature != null && !signature.isEmpty() && signer != null && !signer.isInvalid()) {
            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNER, signer.toMultiHash());
            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNATURE, signature);
        }

        serializeKeys((SecureMetadata) metadata, jsonGenerator);

        jsonGenerator.writeEndObject();
    }
    
}

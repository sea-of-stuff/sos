package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.SecureAtom;

import java.io.IOException;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.serializeKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifestSerializer extends JsonSerializer<SecureAtom> {

    @Override
    public void serialize(SecureAtom atomManifest, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, atomManifest.getType().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, atomManifest.guid().toMultiHash());

        jsonGenerator.writeFieldName(JSONConstants.KEY_LOCATIONS);
        jsonGenerator.writeStartArray();
        serializeLocations(atomManifest, jsonGenerator);
        jsonGenerator.writeEndArray();

        serializeKeys(atomManifest, jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    private void serializeLocations(SecureAtom atomManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<LocationBundle> locations = atomManifest.getLocations();
        for(LocationBundle location:locations) {
            jsonGenerator.writeObject(location);
        }
    }

}

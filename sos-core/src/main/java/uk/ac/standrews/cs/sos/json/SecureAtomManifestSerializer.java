package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.guid.BASE;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;

import java.io.IOException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifestSerializer extends JsonSerializer<SecureAtomManifest> {
    @Override
    public void serialize(SecureAtomManifest atomManifest, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, ManifestType.ATOM.toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, atomManifest.guid().toMultiHash(BASE.HEX)); // HASH(d')

        jsonGenerator.writeFieldName(JSONConstants.KEY_LOCATIONS);
        jsonGenerator.writeStartArray();
        serializeLocations(atomManifest, jsonGenerator);
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName(JSONConstants.KEY_KEYS);
        jsonGenerator.writeStartArray();

        jsonGenerator.writeEndArray();
        serializeKeys();
        jsonGenerator.writeEndObject();
    }

    private void serializeLocations(SecureAtomManifest atomManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<LocationBundle> locations = atomManifest.getLocations();
        for(LocationBundle location:locations) {
            jsonGenerator.writeObject(location);
        }
    }

    /*
     * [
     *      {
     *          "Key" : public-key,
     *          "Role" : role-guid
     *      }
     * ]
     */
    private void serializeKeys() {

    }
}

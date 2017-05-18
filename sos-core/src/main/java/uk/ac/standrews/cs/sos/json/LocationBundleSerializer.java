package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.ManifestConstants;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleSerializer extends JsonSerializer<LocationBundle> {

    @Override
    public void serialize(LocationBundle locationBundle, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(ManifestConstants.BUNDLE_TYPE, locationBundle.getType().toString());
        jsonGenerator.writeStringField(ManifestConstants.BUNDLE_LOCATION, locationBundle.getLocation().toString());

        jsonGenerator.writeEndObject();
    }
}

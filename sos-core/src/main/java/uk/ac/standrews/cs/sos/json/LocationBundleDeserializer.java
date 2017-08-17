package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.locations.LocationFactory;
import uk.ac.standrews.cs.sos.impl.locations.bundles.*;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleDeserializer extends JsonDeserializer<LocationBundle> {

    @Override
    public LocationBundle deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String type = node.get(JSONConstants.BUNDLE_TYPE).textValue();
        String uri = node.get(JSONConstants.BUNDLE_LOCATION).textValue();

        return makeLocationBundle(type, uri);
    }

    private LocationBundle makeLocationBundle(String type, String uri) throws IOException {
        Location location = LocationFactory.makeLocation(uri);

        LocationBundle ret;
        if (type.equals(BundleTypes.CACHE.toString())) {
            ret = new CacheLocationBundle(location);
        } else if (type.equals(BundleTypes.EXTERNAL.toString())) {
            ret = new ExternalLocationBundle(location);
        } else if (type.equals(BundleTypes.PERSISTENT.toString())) {
            ret = new PersistLocationBundle(location);
        } else {
            throw new IOException("Unknown location bundle type exception");
        }

        return ret;
    }

}

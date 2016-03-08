package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.ProvenanceLocationBundle;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleDeserializer implements JsonDeserializer<LocationBundle> {

    @Override
    public LocationBundle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LocationBundle ret = null;

        JsonObject obj = json.getAsJsonObject();

        String type = obj.get(ManifestConstants.BUNDLE_TYPE).getAsString();
        String uri = obj.get(ManifestConstants.BUNDLE_LOCATION).getAsString();

        Location location = null;
        try {
            if (uri.startsWith("sos")) {
                location = new SOSLocation(uri);
            } else {
                location = new URILocation(uri);
            }
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        switch(type) {
            case BundleTypes.CACHE:
                ret = new CacheLocationBundle(location);
                break;
            case BundleTypes.PROVENANCE:
                ret = new ProvenanceLocationBundle(location);
                break;
            default:
                throw new JsonParseException("Unknown location bundle type exception");
        }

        return ret;
    }
}

package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.OldLocation;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestDeserializer implements JsonDeserializer<AtomManifest> {

    @Override
    public AtomManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        JsonArray jLocations = obj.getAsJsonArray(ManifestConstants.KEY_LOCATIONS);
        Collection<LocationBundle> locations = new ArrayList<LocationBundle>();
        for (int i = 0; i < jLocations.size(); i++) {
            JsonElement jLocation = jLocations.get(i);
            String sLocation = jLocation.getAsString();
            try {
                locations.add(new LocationBundle(sLocation)); // FIXME
            } catch (URISyntaxException e) {
                throw new JsonParseException("Unable to create location : " + sLocation);
            }
        }

        AtomManifest manifest = new AtomManifest();
        manifest.setLocations(locations);

        return manifest;
    }
}

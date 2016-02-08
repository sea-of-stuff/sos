package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestDeserializer implements JsonDeserializer<AtomManifest> {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(LocationBundle.class, new LocationBundleDeserializer()).create();

    @Override
    public AtomManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        JsonArray jLocationBundles = obj.getAsJsonArray(ManifestConstants.KEY_LOCATIONS);
        Collection<LocationBundle> bundles = new ArrayList<LocationBundle>();
        for (int i = 0; i < jLocationBundles.size(); i++) {
            LocationBundle bundle = gson.fromJson(jLocationBundles.get(i), LocationBundle.class);
            bundles.add(bundle);
        }

        AtomManifest manifest = new AtomManifest();
        manifest.setLocations(bundles);

        return manifest;
    }
}

package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestDeserializer extends CommonDeserializer implements JsonDeserializer<AtomManifest> {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocationBundle.class, new LocationBundleDeserializer()).create();

    @Override
    public AtomManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        try {
            IGUID contentGUID = getGUID(obj, ManifestConstants.KEY_CONTENT_GUID);

            JsonArray jLocationBundles = obj.getAsJsonArray(ManifestConstants.KEY_LOCATIONS);
            Collection<LocationBundle> bundles = new ArrayList<>();
            for (int i = 0; i < jLocationBundles.size(); i++) {
                LocationBundle bundle = gson.fromJson(jLocationBundles.get(i), LocationBundle.class);
                bundles.add(bundle);
            }

            return new AtomManifest(contentGUID, bundles);
        } catch (GUIDGenerationException e) {
            throw new JsonParseException("Could not recreated GUIDs");
        }
    }
}

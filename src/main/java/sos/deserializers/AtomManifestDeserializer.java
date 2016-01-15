package sos.deserializers;

import com.google.gson.*;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.implementations.utils.Location;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestDeserializer implements JsonDeserializer<AtomManifest> {

    @Override
    public AtomManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();

        JsonElement jGUID = obj.get(ManifestConstants.KEY_CONTENT_GUID);
        String sGUID = jGUID.getAsString();
        GUID guid = new GUIDsha1(sGUID);

        JsonArray jLocations = obj.getAsJsonArray(ManifestConstants.KEY_LOCATIONS);

        Collection<Location> locations = new ArrayList<Location>();
        for (int i = 0; i < jLocations.size(); i++) {
            JsonElement jLocation = jLocations.get(i);
            String sLocation = jLocation.getAsString();
            try {
                locations.add(new Location(sLocation));
            } catch (MalformedURLException e) {
                throw new JsonParseException("Unable to create location : " + sLocation);
            }
        }

        AtomManifest manifest = new AtomManifest();
        manifest.setContentGUID(guid);
        manifest.setLocations(locations);

        return manifest;
    }
}

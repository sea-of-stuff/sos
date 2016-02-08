package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;

import java.lang.reflect.Type;
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


        Set<Map.Entry<String,JsonElement>> entrySet=obj.entrySet();
        for(Map.Entry<String,JsonElement> entry:entrySet){
            String type = entry.getKey();


            JsonArray uris = entry.getValue().getAsJsonArray();
            Location[] locations = new Location[uris.size()];
            for(int i = 0; i < uris.size(); i++) {
                String location = uris.get(i).getAsString();
                try {
                    if (location.startsWith("sos")) {
                        locations[i] = new SOSLocation(location);
                    } else {
                        locations[i] = new URILocation(location);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            ret = new LocationBundle(type, locations);

        }

        return ret;
    }
}

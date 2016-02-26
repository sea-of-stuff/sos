package uk.ac.standrews.cs.sos.model.implementations.locations.bundles;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class LocationBundle {

    // i.e. cache, prov, chunks, etc.
    private String type;

    // i.e. http://abc.com/123, sos://af318/492jv, etc.
    private Location[] locations;

    protected LocationBundle(String type, Location[] locations) {
        this.type = type;
        this.locations = locations;
    }

    public String getType() {
        return type;
    }

    public Location[] getLocations() {
        return locations;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        JsonObject obj = toJSON();
        return gson.toJson(obj);
    }

    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();

        JsonArray array = new JsonArray();
        for(Location location:locations) {
            array.add(location.toString());
        }
        obj.add(type, array);

        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationBundle that = (LocationBundle) o;
        return Objects.equals(type, that.type) &&
                Arrays.equals(locations, that.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, Arrays.hashCode(locations));
    }
}

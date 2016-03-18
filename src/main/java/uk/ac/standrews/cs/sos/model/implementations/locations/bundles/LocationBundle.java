package uk.ac.standrews.cs.sos.model.implementations.locations.bundles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.manifests.ManifestConstants;

import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class LocationBundle {

    // i.e. cache, prov, chunks, etc.
    final private String type;

    // i.e. http://abc.com/123, sos://af318/492jv, etc.
    final private Location location;

    protected LocationBundle(String type, Location location) {
        this.type = type;
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        JsonObject obj = toJSON();
        return gson.toJson(obj);
    }

    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty(ManifestConstants.BUNDLE_TYPE, type);
        obj.addProperty(ManifestConstants.BUNDLE_LOCATION, location.toString());

        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationBundle that = (LocationBundle) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, location);
    }
}

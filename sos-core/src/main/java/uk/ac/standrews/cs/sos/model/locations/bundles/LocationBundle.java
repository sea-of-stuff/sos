package uk.ac.standrews.cs.sos.model.locations.bundles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.interfaces.model.Location;
import uk.ac.standrews.cs.sos.json.LocationBundleDeserializer;
import uk.ac.standrews.cs.sos.json.LocationBundleSerializer;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = LocationBundleSerializer.class)
@JsonDeserialize(using = LocationBundleDeserializer.class)
public abstract class LocationBundle {

    // i.e. cache, prov, chunks, etc.
    final private BundleType type;

    // i.e. http://abc.com/123, sos://af318/492jv, etc.
    final private Location location;

    protected LocationBundle(BundleType type, Location location) {
        this.type = type;
        this.location = location;
    }

    public BundleType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
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

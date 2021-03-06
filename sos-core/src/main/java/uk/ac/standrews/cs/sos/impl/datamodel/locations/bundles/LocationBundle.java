/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.impl.json.LocationBundleDeserializer;
import uk.ac.standrews.cs.sos.impl.json.LocationBundleSerializer;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = LocationBundleSerializer.class)
@JsonDeserialize(using = LocationBundleDeserializer.class)
public class LocationBundle {

    // i.e. cache, prov, chunks, etc.
    final private BundleType type;

    // i.e. http://abc.com/123, sos://af318/492jv, etc.
    final private Location location;

    public LocationBundle(BundleType type, Location location) {
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
            return JSONHelper.jsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "INVALID LOCATION";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof LocationBundle)) return false;
        LocationBundle that = (LocationBundle) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, location);
    }
}

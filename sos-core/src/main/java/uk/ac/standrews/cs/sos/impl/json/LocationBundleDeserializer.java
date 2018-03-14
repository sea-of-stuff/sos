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
package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.LocationFactory;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.*;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleDeserializer extends JsonDeserializer<LocationBundle> {

    @Override
    public LocationBundle deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String type = node.get(JSONConstants.BUNDLE_TYPE).textValue();
        String uri = node.get(JSONConstants.BUNDLE_LOCATION).textValue();

        return makeLocationBundle(type, uri);
    }

    private LocationBundle makeLocationBundle(String type, String uri) throws IOException {
        Location location = LocationFactory.makeLocation(uri);

        LocationBundle ret;
        if (type.equals(BundleTypes.CACHE.toString())) {
            ret = new CacheLocationBundle(location);
        } else if (type.equals(BundleTypes.EXTERNAL.toString())) {
            ret = new ExternalLocationBundle(location);
        } else if (type.equals(BundleTypes.PERSISTENT.toString())) {
            ret = new PersistLocationBundle(location);
        } else {
            throw new IOException("Unknown location bundle type exception");
        }

        return ret;
    }

}

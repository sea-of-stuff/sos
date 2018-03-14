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
package uk.ac.standrews.cs.sos.impl.datamodel.locations;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.Location;

import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleTest extends SetUpTest {

    private static final String EXPECTED_LOCATION =
            "{\"type\":\"cache\"," +
                    "\"location\":\"http://abc.com/123\"}";

    private static final String EXPECTED_PROV_LOCATION =
            "{\"type\":\"external\"," +
                    "\"location\":\"http://abc.com/123/1\"}";

    @Test
    public void toStringTest() throws URISyntaxException, JSONException {
        Location location = new URILocation("http://abc.com/123");
        LocationBundle bundle = new CacheLocationBundle(location);
        JSONAssert.assertEquals(EXPECTED_LOCATION, bundle.toString(), true);
    }

    @Test
    public void toStringProvLocationTest() throws URISyntaxException, JSONException {
        Location location = new URILocation("http://abc.com/123/1");
        LocationBundle bundle = new ExternalLocationBundle(location);
        JSONAssert.assertEquals(EXPECTED_PROV_LOCATION, bundle.toString(), true);
    }

}
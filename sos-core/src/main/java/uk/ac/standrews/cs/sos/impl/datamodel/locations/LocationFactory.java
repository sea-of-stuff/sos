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

import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static uk.ac.standrews.cs.sos.constants.LocationSchemes.SOS_SCHEME;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationFactory {

    public static Location makeLocation(String uri) throws IOException {
        Location location;
        try {
            if (uri.startsWith(SOS_SCHEME)) {
                location = new SOSLocation(uri);
            } else {
                location = new URILocation(uri);
            }
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IOException(e);
        }

        return location;
    }
}

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
package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;

import java.io.IOException;
import java.io.Serializable;
import java.util.Queue;

/**
 * Maps entities and locations
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocationsIndex extends Serializable {

    void addLocation(IGUID guid, LocationBundle locationBundle);

    Queue<LocationBundle> findLocations(IGUID guid);

    void persist(IFile file) throws IOException;

    void deleteLocation(IGUID node, IGUID guid);

    void clear();
}

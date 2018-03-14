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
package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.json.AtomManifestDeserializer;
import uk.ac.standrews.cs.sos.impl.json.AtomManifestSerializer;

import java.util.Set;

/**
 * This is the interface for the Atom manifest.
 * An atom is the basic building block for the SOS and it is used to abstract data over locations.
 *
 * Example:
 *
 * {
 *  "Type" : "Atom",
 *  "guid" : "da39a3ee5e6b4b0d3255bfef95601890afd80709",
 *  "locations" : [
 *      {
 *      "Type" : "cache",
 *      "Location" : "sos://3c9bfd93ab9a6e2ed501fc583685088cca66bac2/da39a3ee5e6b4b0d3255bfef95601890afd80709"
 *      }
 *  ]
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = AtomManifestSerializer.class)
@JsonDeserialize(using = AtomManifestDeserializer.class)
public interface Atom extends Manifest {

    /**
     * Get the locations for this atom.
     *
     * @return locations of this atom
     */
    Set<LocationBundle> getLocations();

    /**
     * Return the atom's data
     *
     * @return the data for the atom
     */
    Data getData();

    /**
     * WIP: Algorithm used to compress data
     * @return
     */
    CompressionAlgorithms getCompressionAlgorithm();

    boolean verifyIntegrity(LocationBundle locationBundle);
}

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
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.CompoundManifestDeserializer;
import uk.ac.standrews.cs.sos.impl.json.CompoundManifestSerializer;

import java.util.Set;

/**
 * A compound serves as an aggregator of atoms, compounds and versions.
 *
 * Example:
 *
 * {
 *  "Type" : "Compound",
 *  "compound_type" : "COLLECTION",
 *  "guid" : "cba74f828335fa96298f5efb3b2bf669ddc91031",
 *  "contents" : [
 *      {
 *          "label" : "folder",
 *          "guid" : "606c92e9707fd89d288c198b28f6cf3079be63bd"
 *      },
 *      {
 *          "guid" : "3ee75808c94ab7f53188e91a71cfa2bdfbcd1ebc"
 *      }
 *  ],
 *  "signature" : "MCwCFHE36niavy6cRQjEk6dd8oBlGkXXAhQQus9CIRZWCEoGDKydiuA6N/51Eg=="
 * }
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = CompoundManifestSerializer.class)
@JsonDeserialize(using = CompoundManifestDeserializer.class)
public interface Compound extends SignedManifest {

    /**
     * Get the contents of this compound.
     *
     * @return the contents of this compound
     */
    Set<Content> getContents();

    /**
     * Get the content of the compound matching this label
     *
     * @param label of the content to get
     * @return content. Null if not found
     */
    Content getContent(String label);

    /**
     * Get the content of the compound matching this guid
     *
     * @param guid of the content to get
     * @return content. Null if not found
     */
    Content getContent(IGUID guid);

    /**
     * Get the type of compound.
     *
     * @return the compound type
     */
    CompoundType getCompoundType();
}

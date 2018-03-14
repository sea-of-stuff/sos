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
import uk.ac.standrews.cs.sos.impl.json.MetadataDeserializer;
import uk.ac.standrews.cs.sos.impl.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.impl.metadata.Property;

/**
 *
 * Example:
 *
 * {
 *  "guid" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *  "Type" : "metadata",
 *  "Properties" : [
 *      {
 *          "key" : "Owner",
 *          "Type" : "guid",
 *          "Value" : "abb134200a"
 *      },
 *      {
 *          "key" : "Size",
 *          "Type" : "long",
 *          "Value" : 105
 *      },
 *      {
 *          "key" : "Timestamp",
 *          "Type" : "long",
 *          "Value" : 1487606187
 *      },
 *      {
 *          "key" : "Content-Type",
 *          "Type" : "string",
 *          "Value" : "application/octet-stream"
 *      }
 *   ]
 * }
 *
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = MetadataDeserializer.class)
@JsonSerialize(using = MetadataSerializer.class)
public interface Metadata extends SignedManifest {

    Property getProperty(String propertyName);

    boolean hasProperty(String propertyName);

    String[] getAllPropertyNames();

}

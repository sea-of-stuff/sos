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
package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.NodesCollection;

/**
 * Metadata Management Service (MMS)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataService extends Service {

    /**
     * Computes the metadata for some given data
     *
     * @param metadataBuilder containing info for metadata
     * @return metadata object
     * @throws MetadataException if metadata could not be processed
     */
    Metadata processMetadata(MetadataBuilder metadataBuilder) throws MetadataException;

    /**
     * Add the given metadata to the sea of stuff
     *
     * @param metadata to be added to the sea of stuff
     */
    void addMetadata(Metadata metadata) throws MetadataPersistException;

    /**
     * Get the metadata that matches the given GUID.
     *
     * @param guid of the metadata
     * @return metadata associated with the GUID
     * @throws MetadataNotFoundException if not found
     */
    Metadata getMetadata(IGUID guid) throws MetadataNotFoundException;

    /**
     * Get the metadata that matches the given GUID and that is stored within a given nodes collection
     *
     * @param nodesCollection within which the metadata should be stored
     * @param guid of the metadata
     * @return the metadata
     * @throws MetadataNotFoundException if the metadata could not be found
     */
    Metadata getMetadata(NodesCollection nodesCollection, IGUID guid) throws MetadataNotFoundException;
}

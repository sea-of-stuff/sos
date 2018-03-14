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
import uk.ac.standrews.cs.sos.impl.json.NodesCollectionDeserializer;
import uk.ac.standrews.cs.sos.impl.json.NodesCollectionSerializer;

import java.util.Set;

/**
 * A NodesCollection is an easy way to represent a collection of multiple nodes.
 * Note, however, that a NodesCollection holds references to the nodes only. It does not hold any information about the node themselves.
 *
 * We identify three types of collections:
 * - LOCAL: this is the node that this SOS instance is running on
 * - SPECIFIED: this is a set of nodes specified by the creator of the collection
 * - ANY: it has no references, since any node in the SOS belongs to this collection
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = NodesCollectionSerializer.class)
@JsonDeserialize(using = NodesCollectionDeserializer.class)
public interface NodesCollection {

    /**
     * Returns the refs of the nodes available from within this scope
     * if the type is SPECIFIED
     *
     * If the type is LOCAL or ANY, the set of nodes returned is empty.
     *
     * @return a set of refs to nodes
     */
    Set<IGUID> nodesRefs();

    /**
     * Add node ref to collection (collection must be of type SPECIFIED)
     *
     * @param nodeRef to add
     */
    void addNodeRef(IGUID nodeRef);

    /**
     * Return the size of this nodes collection
     * @return size of this nodes collection
     */
    int size();

    /**
     * Get the type of the collection.
     *
     * @return the type of this nodes collection
     */
    NodesCollectionType type();

    /**
     * Unique string representing this nodes collection
     * @return string for this nodes collection
     */
    String toUniqueString();

    /**
     * Shuffle this nodes collection
     */
    void shuffle();

}

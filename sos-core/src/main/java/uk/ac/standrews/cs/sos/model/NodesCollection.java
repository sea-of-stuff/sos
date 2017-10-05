package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.json.NodesCollectionDeserializer;
import uk.ac.standrews.cs.sos.json.NodesCollectionSerializer;

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
     * Get the type of the collection.
     *
     * @return the type of this nodes collection
     */
    NodesCollectionType type();

    String toUniqueString();

}

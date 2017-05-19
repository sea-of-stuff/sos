package uk.ac.standrews.cs.sos.model;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NodesCollection {

    /**
     * Returns the nodes available from within this scope
     * if the type is SPECIFIED
     *
     * If the type is LOCAL or ANY, the set of nodes returned is empty.
     *
     * @return a set of nodes
     */
    Set<Node> nodes();

    /**
     * Get the type of the collection.
     *
     * @return the type of this nodes collection
     */
    TYPE type();

    enum TYPE {
        LOCAL, // This local node
        SPECIFIED, // The collection is limited to the specified nodes
        ANY // The collection is unlimited
    }

}

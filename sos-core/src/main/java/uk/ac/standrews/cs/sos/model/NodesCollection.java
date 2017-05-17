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
     * NOTE: will use the NDS to actually find the nodes
     *
     * @return
     */
    Set<Node> nodes();

    /**
     * Get the type of the collection.
     *
     * @return
     */
    TYPE type();

    enum TYPE {
        LOCAL,
        ANY, // The collection is unlimited
        SPECIFIED // The collection is limited to the specified nodes
    }

}

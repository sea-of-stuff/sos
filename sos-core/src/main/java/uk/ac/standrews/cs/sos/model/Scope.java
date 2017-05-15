package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Scope {

    /**
     * Random GUID used to identify this scope
     *
     * @return
     */
    IGUID guid();

    /**
     * Returns the nodes available from within this scope
     * if the type is RESTRICTED
     *
     * NOTE: will use the NDS to actually find the nodes
     *
     * @return
     */
    Node[] nodes();

    /**
     * Get the type of scope.
     *
     * @return
     */
    TYPE type();

    enum TYPE {
        ANY, // The Scope is unlimited
        RESTRICTED // The scope is limited to the specified nodes
    }
}
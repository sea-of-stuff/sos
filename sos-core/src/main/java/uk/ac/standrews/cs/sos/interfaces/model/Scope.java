package uk.ac.standrews.cs.sos.interfaces.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Scope {

    /**
     * Returns the nodes available from within this scope
     * if the type is RESTRICTED
     *
     * @return
     */
    Node[] nodes();

    TYPES type();

    enum TYPES {
        ANY, RESTRICTED
    }
}

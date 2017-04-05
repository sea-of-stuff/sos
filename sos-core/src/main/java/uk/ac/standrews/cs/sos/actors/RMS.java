package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.Role;

/**
 * Role Management Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface RMS {

    /**
     * Add the specified role to the directory of roles
     *
     * @param role
     */
    void add(Role role);

    /**
     * Get the role with the specified GUID
     *
     * @param guid
     * @return
     */
    Role get(IGUID guid);

    /**
     * Returns the active role for this node
     *
     * @return
     */
    Role active();

    /**
     * Sets the active role for this node
     *
     * @param role
     * @return
     */
    Role setActive(Role role);
}

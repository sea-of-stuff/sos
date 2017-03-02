package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.roles.Role;

/**
 * Role Management Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface RMS {

    void add(Role role);

    Role get(IGUID guid);
}

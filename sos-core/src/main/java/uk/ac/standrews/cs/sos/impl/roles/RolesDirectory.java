package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.IGUID;

import java.util.HashMap;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RolesDirectory {

    private HashMap<IGUID, RoleImpl> roles; // TODO just use db, no need to have any information in memory

    public RolesDirectory() {
        // TODO - load any existing directory
        roles = new HashMap<>();
    }

    private void addRole(RoleImpl role) {
        roles.put(role.guid(), role);
    }

    private RoleImpl getRole(IGUID roleGUID) {
        return roles.get(roleGUID);
    }
}

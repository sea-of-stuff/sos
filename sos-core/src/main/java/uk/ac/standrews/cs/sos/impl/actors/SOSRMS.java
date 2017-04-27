package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.RMS;
import uk.ac.standrews.cs.sos.model.Role;

import java.util.HashMap;
import java.util.Set;

/**
 * TODO - persistence -> cache, local, remote
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSRMS implements RMS {

    private HashMap<IGUID, Role> roles;
    private HashMap<IGUID, Set<IGUID>> usersToRoles;
    private Role activeRole;

    private SOSRMS() {
        roles = new HashMap<>();
        usersToRoles = new HashMap<>();
    }

    private static SOSRMS instance;
    public static SOSRMS instance() {
        if (instance == null) {
            instance = new SOSRMS();
        }

        return instance;
    }

    @Override
    public void addRole(Role role) {
        roles.put(role.guid(), role);
    }

    @Override
    public Role getRole(IGUID roleGUID) {
        return roles.get(roleGUID);
    }

    @Override
    public Role[] getRoles(IGUID userGUID) {
        return new Role[0];
    }

    @Override
    public Role active() {
        return activeRole;
    }

    @Override
    public void setActive(Role role) {
        this.activeRole = role;
    }

}

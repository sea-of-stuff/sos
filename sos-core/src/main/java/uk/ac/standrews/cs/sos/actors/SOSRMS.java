package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.actors.RMS;
import uk.ac.standrews.cs.sos.interfaces.model.Role;
import uk.ac.standrews.cs.sos.model.roles.RoleImpl;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSRMS implements RMS {

    private HashMap<IGUID, Role> roles;
    private Role activeRole;

    public SOSRMS() {
        roles = new LinkedHashMap<>();

        // FIXME Create new active role by default, if none exists
        activeRole = new RoleImpl((PublicKey) null, "simone", "sic2@st-andrews.ac.uk");
        roles.put(activeRole.guid(), activeRole);
    }

    @Override
    public void add(Role role) {
        roles.put(role.guid(), role);
    }

    @Override
    public Role get(IGUID guid) {

        // TODO - cache, local, remote

        return roles.get(guid);
    }

    @Override
    public Role active() {
        return activeRole;
    }

    @Override
    public Role setActive(Role role) {
        return activeRole = role;
    }

}

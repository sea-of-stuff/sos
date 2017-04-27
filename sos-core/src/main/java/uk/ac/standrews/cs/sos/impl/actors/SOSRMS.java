package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.RMS;
import uk.ac.standrews.cs.sos.model.Role;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * TODO - persistence -> cache, local, remote
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSRMS implements RMS {

    private HashMap<IGUID, Role> roles;
    private Role activeRole;

    private SOSRMS() {
        roles = new LinkedHashMap<>();
    }

    private static SOSRMS instance;
    public static SOSRMS instance() {
        if (instance == null) {
            instance = new SOSRMS();
        }

        return instance;
    }

    @Override
    public void add(Role role) {
        roles.put(role.guid(), role);
    }

    @Override
    public Role get(IGUID guid) {
        return roles.get(guid);
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

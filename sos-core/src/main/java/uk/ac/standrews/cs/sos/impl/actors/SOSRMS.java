package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.RMS;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO - persistence -> cache, local, remote
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSRMS implements RMS {

    private HashMap<IGUID, User> users;
    private HashMap<IGUID, Role> roles;
    private HashMap<IGUID, Set<IGUID>> usersToRoles;
    private Role activeRole;

    private SOSRMS() {
        users = new HashMap<>();
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
    public void addUser(User user) {
        users.put(user.guid(), user);
    }

    @Override
    public User getUser(IGUID userGUID) {
        return users.get(userGUID);
    }

    @Override
    public void addRole(Role role) {

        if (!usersToRoles.containsKey(role.getUser())) {
            usersToRoles.put(role.getUser(), new LinkedHashSet<>());
        }

        usersToRoles.get(role.getUser()).add(role.guid());


        roles.put(role.guid(), role);
    }

    @Override
    public Role getRole(IGUID roleGUID) {
        return roles.get(roleGUID);
    }

    @Override
    public Set<Role> getRoles(IGUID userGUID) {
        return usersToRoles.get(userGUID).stream()
                .map(u -> roles.get(u))
                .collect(Collectors.toSet());
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

package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UsersRolesCache implements UsersRolesService, Serializable {

    private transient HashMap<IGUID, User> users;
    private transient HashMap<IGUID, Role> roles;
    private transient HashMap<IGUID, Set<IGUID>> usersToRoles;
    private transient Role activeRole;
    private transient User activeUser;

    public UsersRolesCache() {

        users = new HashMap<>();
        roles = new HashMap<>();
        usersToRoles = new HashMap<>();
    }

    @Override
    public void addUser(User user) {
        users.put(user.guid(), user);
    }

    @Override
    public User getUser(IGUID userGUID) throws UserNotFoundException {

        if (!users.containsKey(userGUID)) throw new UserNotFoundException();

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
    public Role getRole(IGUID roleGUID) throws RoleNotFoundException {

        if (!roles.containsKey(roleGUID)) throw new RoleNotFoundException();

        return roles.get(roleGUID);
    }

    @Override
    public Set<Role> getRoles(IGUID userGUID) {

        return usersToRoles.get(userGUID)
                .stream()
                .map(u -> roles.get(u))
                .collect(Collectors.toSet());
    }

    @Override
    public Role activeRole() throws RoleNotFoundException {

        if (activeRole == null) throw new RoleNotFoundException();

        return activeRole;
    }

    @Override
    public void setActiveRole(Role role) {

        addRole(role);
        this.activeRole = role;
    }

    @Override
    public User activeUser() throws UserNotFoundException {

        if (activeUser == null) throw new UserNotFoundException();

        return activeUser;
    }

    @Override
    public void setActiveUser(User user) {

        addUser(user);
        this.activeUser = user;
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

//        out.writeInt(lru.size());
//        for (IGUID guid : lru) {
//            out.writeUTF(guid.toString());
//        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

//        int lruSize = in.readInt();
//        lru = new ConcurrentLinkedQueue<>();
//        for(int i = 0; i < lruSize; i++) {
//            String guid = in.readUTF();
//            try {
//                lru.add(GUIDFactory.recreateGUID(guid));
//            } catch (GUIDGenerationException e) {
//                SOS_LOG.log(LEVEL.WARN, "Manifest cache loading - unable to created GUID for entry: " + guid);
//            }
//        }
//
//        cache = new HashMap<>();
    }
}

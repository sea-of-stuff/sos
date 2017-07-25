package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.utils.FileUtils.RoleFromString;
import static uk.ac.standrews.cs.sos.utils.FileUtils.UserFromString;

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
    public Set<User> getUsers() {
        return users.keySet().stream().map(u -> users.get(u)).collect(Collectors.toSet());
    }

    @Override
    public Set<Role> getRoles() {
        return roles.keySet().stream().map(u -> roles.get(u)).collect(Collectors.toSet());
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

    @Override
    public void flush() {
        // NOTE: This method is not implemented, as we use the persist method to actually flush the cache
    }

    public static UsersRolesCache load(IFile file) throws IOException, ClassNotFoundException {

        UsersRolesCache persistedCache = (UsersRolesCache) Persistence.Load(file);
        if (persistedCache == null) throw new ClassNotFoundException();

        return persistedCache;
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();


        if (activeUser != null) {
            out.writeBoolean(true);
            out.writeUTF(activeUser.toString());
        } else {
            out.writeBoolean(false);
        }

        if (activeRole != null) {
            out.writeBoolean(true);
            out.writeUTF(activeRole.toString());
        } else {
            out.writeBoolean(false);
        }

        out.writeInt(users.size());
        for(Map.Entry<IGUID, User> pair : users.entrySet()) {
            out.writeUTF(pair.getValue().toString());
        }

        out.writeInt(roles.size());
        for(Map.Entry<IGUID, Role> pair : roles.entrySet()) {
            out.writeUTF(pair.getValue().toString());
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
            if (in.readBoolean()) activeUser = UserFromString(in.readUTF());
            if (in.readBoolean()) activeRole = RoleFromString(in.readUTF());

            users = new HashMap<>();
            int noUsers = in.readInt();
            for(int i = 0; i < noUsers; i++) {
                User user = UserFromString(in.readUTF());
                addUser(user);
            }

            roles = new HashMap<>();
            usersToRoles = new HashMap<>();
            int noRoles = in.readInt();
            for(int i = 0; i < noRoles; i++) {
                Role role = RoleFromString(in.readUTF());
                addRole(role);
            }


        } catch (UserNotFoundException | RoleNotFoundException e) {
            throw new IOException(e);
        }

    }
}

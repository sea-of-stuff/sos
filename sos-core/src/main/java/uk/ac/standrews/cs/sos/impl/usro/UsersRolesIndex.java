package uk.ac.standrews.cs.sos.impl.usro;

import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static uk.ac.standrews.cs.sos.utils.FileUtils.RoleFromString;
import static uk.ac.standrews.cs.sos.utils.FileUtils.UserFromString;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UsersRolesIndex implements Serializable {

    private transient HashMap<IGUID, Set<IGUID>> usersToRoles;
    private transient Role activeRole;
    private transient User activeUser;

    public UsersRolesIndex() {

        usersToRoles = new HashMap<>();
    }

    public void addRole(Role role) {

        if (!usersToRoles.containsKey(role.getUser())) {
            usersToRoles.put(role.getUser(), new LinkedHashSet<>());
        }

        usersToRoles.get(role.getUser()).add(role.guid());
    }

    public Set<IGUID> getRoles(IGUID userGUID) {

        return usersToRoles.get(userGUID);
    }

    public Role activeRole() throws RoleNotFoundException {

        if (activeRole == null) throw new RoleNotFoundException();

        return activeRole;
    }

    public void setActiveRole(Role role) {

        addRole(role);
        this.activeRole = role;
    }

    public User activeUser() throws UserNotFoundException {

        if (activeUser == null) throw new UserNotFoundException();

        return activeUser;
    }

    public void setActiveUser(User user) {

        this.activeUser = user;
    }

    public static UsersRolesIndex load(IFile file) throws IOException, ClassNotFoundException {

        UsersRolesIndex persistedCache = (UsersRolesIndex) Persistence.Load(file);
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

        out.writeInt(usersToRoles.size());
        for(Map.Entry<IGUID, Set<IGUID>> u2r:usersToRoles.entrySet()) {
            out.writeUTF(u2r.getKey().toMultiHash());
            out.writeInt(u2r.getValue().size());
            for(IGUID role:u2r.getValue()) {
                out.writeUTF(role.toMultiHash());
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
            if (in.readBoolean()) activeUser = UserFromString(in.readUTF());
            if (in.readBoolean()) activeRole = RoleFromString(in.readUTF());

            usersToRoles = new HashMap<>();
            int numberOfUsers = in.readInt();
            for(int i = 0; i < numberOfUsers; i++) {
                IGUID userGUID = GUIDFactory.recreateGUID(in.readUTF());
                usersToRoles.put(userGUID, new LinkedHashSet<>());

                int numberOfRoles = in.readInt();
                for(int j = 0; j < numberOfRoles; j++) {
                    IGUID roleGUID = GUIDFactory.recreateGUID(in.readUTF());
                    usersToRoles.get(userGUID).add(roleGUID);
                }
            }

        } catch (GUIDGenerationException | UserNotFoundException | RoleNotFoundException e) {
            throw new IOException(e);
        }

    }
}

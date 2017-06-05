package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.exceptions.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.roles.LocalUsersRolesDirectory;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.impl.roles.UsersRolesCache;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.Set;

/**
 * Service to manage Users and Roles.
 *
 * There is only one activeRole Role at a given time
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSUsersRolesService implements UsersRolesService {

    // 1. inMemory cache
    // 2. DOING - local disk
    // 3. TODO - remote
    private UsersRolesCache inMemoryCache;
    private LocalUsersRolesDirectory localDirectory;

    public SOSUsersRolesService(LocalStorage localStorage) {

        inMemoryCache = new UsersRolesCache();
        localDirectory = new LocalUsersRolesDirectory(localStorage);

        try {

            manageDefaultUser();
            manageDefaultRole();

        } catch (SignatureException | UserNotFoundException | ProtectionException e) {
            e.printStackTrace();
        }
    }

    private void manageDefaultUser() throws SignatureException {

        try {
            activeUser();

        } catch (UserNotFoundException e) {

            User defaultUser = new UserImpl("DEFAULT_USER");

            addUser(defaultUser);
            setActiveUser(defaultUser);
        }
    }

    private void manageDefaultRole() throws ProtectionException, SignatureException, UserNotFoundException {

        try {
            activeRole();

        } catch (RoleNotFoundException e) {

            User defaultUser = activeUser();
            Role defaultRole = new RoleImpl(defaultUser, "DEFAULT_ROLE");

            addRole(defaultRole);
            setActiveRole(defaultRole);
        }
    }

    @Override
    public void addUser(User user) {

        inMemoryCache.addUser(user);
        localDirectory.addUser(user);
    }

    @Override
    public User getUser(IGUID userGUID) {

        return inMemoryCache.getUser(userGUID);
    }

    @Override
    public void addRole(Role role) {

        inMemoryCache.addRole(role);
        localDirectory.addRole(role);
    }

    @Override
    public Role getRole(IGUID roleGUID) {

        return inMemoryCache.getRole(roleGUID);
    }

    @Override
    public Set<Role> getRoles(IGUID userGUID) {

        return inMemoryCache.getRoles(userGUID);
    }

    @Override
    public Role activeRole() throws RoleNotFoundException {

        return inMemoryCache.activeRole();
    }

    @Override
    public void setActiveRole(Role role) {

        inMemoryCache.setActiveRole(role);
        localDirectory.setActiveRole(role);
    }

    @Override
    public User activeUser() throws UserNotFoundException {
        return inMemoryCache.activeUser();
    }

    @Override
    public void setActiveUser(User user) {

        inMemoryCache.setActiveUser(user);
        localDirectory.setActiveUser(user);
    }

}

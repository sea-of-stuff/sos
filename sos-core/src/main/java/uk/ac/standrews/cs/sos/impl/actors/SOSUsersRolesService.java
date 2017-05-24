package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.roles.LocalUsersRolesDirectory;
import uk.ac.standrews.cs.sos.impl.roles.UsersRolesCache;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.Set;

/**
 * Service to manage Users and Roles.
 *
 * There is only one active Role at a given time
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
    public Role active() {

        return inMemoryCache.active();
    }

    @Override
    public void setActive(Role role) {

        inMemoryCache.setActive(role);
        localDirectory.setActive(role);
    }

}

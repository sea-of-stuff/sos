package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.impl.roles.UsersRolesDirectory;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.Set;

/**
 * There is only one active Role at a given time
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSUsersRolesService implements UsersRolesService {

    // TODO - need to define two more layers:
    // 1. inMemory cache
    // 2. TODO - local disk
    // 3. TODO - remote
    private UsersRolesDirectory inMemoryCache;

    public SOSUsersRolesService() {

        inMemoryCache = new UsersRolesDirectory();
    }

    @Override
    public void addUser(User user) {

        inMemoryCache.addUser(user);
    }

    @Override
    public User getUser(IGUID userGUID) {
        return inMemoryCache.getUser(userGUID);
    }

    @Override
    public void addRole(Role role) {

        inMemoryCache.addRole(role);
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
    }

}

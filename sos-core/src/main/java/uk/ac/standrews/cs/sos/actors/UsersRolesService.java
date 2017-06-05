package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.UserNotFoundException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.Set;

/**
 * Users and Roles Management/Discovery Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface UsersRolesService {

    /**
     * Add a user to the service
     *
     * @param user
     */
    void addUser(User user);

    /**
     * Get a user from the service
     *
     * @param userGUID
     * @return
     */
    User getUser(IGUID userGUID) throws UserNotFoundException;

    /**
     * Add the specified role to the directory of roles
     *
     * @param role
     */
    void addRole(Role role);

    /**
     * Get the role with the specified GUID
     *
     * @param roleGUID
     * @return
     */
    Role getRole(IGUID roleGUID) throws RoleNotFoundException;

    /**
     *
     * @param userGUID
     * @return
     */
    Set<Role> getRoles(IGUID userGUID) throws RoleNotFoundException;

    /**
     * Returns the activeRole role for this node
     *
     * @return the activeRole role
     */
    Role activeRole() throws RoleNotFoundException;

    /**
     * Sets the activeRole role for this node
     *
     * @param role
     */
    void setActiveRole(Role role);

    User activeUser() throws UserNotFoundException;

    void setActiveUser(User user);
}

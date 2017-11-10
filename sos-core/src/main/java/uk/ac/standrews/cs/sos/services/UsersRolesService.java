package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.ManifestBuilder;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.Set;

/**
 * Users and Roles Management/Discovery Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface UsersRolesService extends Service {

    /**
     * Get the users stored at this node
     *
     * @return set of users
     */
    Set<User> getUsers();

    /**
     * Get the roles stored at this node
     *
     * @return set of roles
     */
    Set<Role> getRoles();

    /**
     * Add a user to the service
     *
     * @param user to be added
     * @throws UserRolePersistException if the user could not be added
     */
    void addUser(User user) throws UserRolePersistException;

    /**
     * Get a user from the service
     *
     * @param userGUID of the user
     * @return user
     * @throws UserNotFoundException if the user could not be found
     */
    User getUser(IGUID userGUID) throws UserNotFoundException;

    /**
     * Add the specified role to the directory of roles
     *
     * @param role to be added
     * @throws UserRolePersistException if the role could not be added
     */
    void addRole(Role role) throws UserRolePersistException;

    /**
     * Get the role with the specified GUID
     *
     * @param roleGUID of the role
     * @return role
     * @throws RoleNotFoundException if the role could not be found
     */
    Role getRole(IGUID roleGUID) throws RoleNotFoundException;

    /**
     * Get the role as specified in the ManifestBuilder.
     * If no role is specified, then return the current active role.
     *
     * @param manifestBuilder in which there is info to get the role
     * @return the role associated with the manifest builder
     * @throws RoleNotFoundException if no role was found
     */
    Role getRole(ManifestBuilder manifestBuilder) throws RoleNotFoundException;

    /**
     * Get the roles associated with the given user
     *
     * @param userGUID of the user
     * @return set of roles created by the user
     * @throws RoleNotFoundException if no role could be found
     */
    Set<Role> getRoles(IGUID userGUID) throws RoleNotFoundException;

    /**
     * Returns the active role for this node
     *
     * @return the active role
     * @throws RoleNotFoundException if no active role is found
     */
    Role activeRole() throws RoleNotFoundException;

    /**
     * Sets the active role for this node
     *
     * @param role to be made active
     * @throws UserRolePersistException if the role could not be made active
     */
    void setActiveRole(Role role) throws UserRolePersistException;

    /**
     * Returns the active user
     *
     * @return the active user
     * @throws UserNotFoundException if no active user is found
     */
    User activeUser() throws UserNotFoundException;

    /**
     * Sets the active user
     *
     * @param user to be made active
     * @throws UserRolePersistException if the user could not be made active
     */
    void setActiveUser(User user) throws UserRolePersistException;

    /**
     * Flushes any in-memory data structures to disk
     */
    void flush();
}

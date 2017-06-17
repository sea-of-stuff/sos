package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.FileUtils;

import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.Internals.ACTIVE_ROLE;
import static uk.ac.standrews.cs.sos.constants.Internals.ACTIVE_USER;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalUsersRolesDirectory implements UsersRolesService {

    private LocalStorage localStorage;

    public LocalUsersRolesDirectory(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public Set<User> getUsers() {
        return null;
    }

    @Override
    public Set<Role> getRoles() {
        return null;
    }

    @Override
    public void addUser(User user) throws UserRolePersistException {

        saveToFile(user);
    }

    @Override
    public User getUser(IGUID userGUID) throws UserNotFoundException {

        return getUserFromGUID(userGUID);
    }

    @Override
    public void addRole(Role role) throws UserRolePersistException {

        saveToFile(role);
    }

    @Override
    public Role getRole(IGUID roleGUID) throws RoleNotFoundException {

        return getRoleFromGUID(roleGUID);
    }

    @Override
    public Set<Role> getRoles(IGUID userGUID) throws RoleNotFoundException {
        return null;
    }

    @Override
    public Role activeRole() throws RoleNotFoundException {

        IGUID guid = getActive(ACTIVE_ROLE);
        return getRole(guid);
    }

    @Override
    public void setActiveRole(Role role) throws UserRolePersistException {

        addRole(role);
        setActive(role, ACTIVE_ROLE);
    }

    @Override
    public User activeUser() throws UserNotFoundException {

        IGUID guid = getActive(ACTIVE_USER);
        return getUser(guid);
    }

    @Override
    public void setActiveUser(User user) throws UserRolePersistException {

        addUser(user);
        setActive(user, ACTIVE_USER);
    }

    @Override
    public void flush() {

    }

    private void setActive(User user, String filename) {

        try {
            IFile file = makeFile(filename);

            file.setData(new StringData(user.guid().toString()));
            file.persist();

        } catch (DataStorageException | PersistenceException | DataException e) {
            e.printStackTrace();
        }
    }

    private IGUID getActive(String filename) {

        try {
            IFile file = makeFile(filename);
            String guidString = new String(file.getData().getState());

            return GUIDFactory.recreateGUID(guidString);

        } catch (DataStorageException | GUIDGenerationException | DataException e) {
            // FIXME - throw exception
            return null;
        }

    }

    private void saveToFile(User user) throws UserRolePersistException {

        try {
            String userGUID = user.guid().toString();

            Data data = new StringData(user.toString());

            IFile file = makeJSONFile(userGUID);
            file.setData(data);
            file.persist();

        } catch (PersistenceException | DataException | DataStorageException e) {
            throw new UserRolePersistException();
        }
    }

    private IFile makeFile(String filename) throws DataStorageException {
        IDirectory usroDir = localStorage.getUsersRolesDirectory();

        return FileUtils.CreateFile(localStorage, usroDir, filename);
    }

    private IFile makeJSONFile(String guid) throws DataStorageException {

        return makeFile(guid + FileUtils.JSON_EXTENSION);
    }

    // TODO - attempt to get also keys?????
    private User getUserFromGUID(IGUID guid) throws UserNotFoundException {
        try {
            IFile file = makeJSONFile(guid.toString());

            return FileUtils.UserFromFile(file);

        } catch (DataStorageException e) {
            throw new UserNotFoundException();
        }
    }

    private Role getRoleFromGUID(IGUID guid) throws RoleNotFoundException {
        try {
            IFile file = makeJSONFile(guid.toString());

            return FileUtils.RoleFromFile(file);

        } catch (DataStorageException e) {
            throw new RoleNotFoundException();
        }
    }
}

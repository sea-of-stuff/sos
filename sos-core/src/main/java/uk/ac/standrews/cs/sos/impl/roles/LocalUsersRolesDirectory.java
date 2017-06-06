package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.exceptions.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.UserRolePersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.manifests.directory.FileUtils;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.Set;

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
        return null;
    }

    @Override
    public void setActiveRole(Role role) {

        try {
            IFile file = makeFile("ACTIVE_ROLE");

            file.setData(new StringData(role.guid().toString()));
            file.persist();

        } catch (DataStorageException | PersistenceException | DataException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User activeUser() throws UserNotFoundException {
        return null;
    }

    @Override
    public void setActiveUser(User user) {

        try {
            IFile file = makeFile("ACTIVE_USER");

            file.setData(new StringData(user.guid().toString()));
            file.persist();

        } catch (DataStorageException | PersistenceException | DataException e) {
            e.printStackTrace();
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

        return FileUtils.File(localStorage, usroDir, filename);
    }

    private IFile makeJSONFile(String guid) throws DataStorageException {

        return makeFile(guid + FileUtils.JSON_EXTENSION);
    }

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

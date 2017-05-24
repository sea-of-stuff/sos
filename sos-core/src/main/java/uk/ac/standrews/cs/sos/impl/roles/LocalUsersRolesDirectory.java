package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsDirectoryException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.manifests.directory.FileUtils;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.util.Set;

/**
 *
 * TODO - files are immutable and should not be updatable?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalUsersRolesDirectory implements UsersRolesService {

    private LocalStorage localStorage;

    public LocalUsersRolesDirectory(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }


    @Override
    public void addUser(User user) {

        try {
            saveToFile(user);
        } catch (ManifestsDirectoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(IGUID userGUID) {
        try {
            return getUserFromGUID(userGUID);
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void addRole(Role role) {

        try {
            saveToFile(role);
        } catch (ManifestsDirectoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Role getRole(IGUID roleGUID) {

        try {
            return getRoleFromGUID(roleGUID);
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Set<Role> getRoles(IGUID userGUID) {
        return null;
    }

    @Override
    public Role active() {
        return null;
    }

    @Override
    public void setActive(Role role) {


        // TODO - make a file in the disk called ACTIVE-GUID
        // delete other files of same format if not matching this role
    }

    private void saveToFile(User user) throws ManifestsDirectoryException {

        try {
            String userGUID = user.guid().toString();

            Data data = new StringData(user.toString());

            IFile file = getFile(userGUID);
            file.setData(data);
            file.persist();

        } catch (PersistenceException | DataException | DataStorageException e) {
            throw new ManifestsDirectoryException(e);
        }
    }

    private IFile getFile(String guid) throws DataStorageException {
        IDirectory usroDir = localStorage.getUsersRolesDirectory();

        return FileUtils.File(localStorage, usroDir, guid);
    }

    private User getUserFromGUID(IGUID guid) throws ManifestNotFoundException { // FIXME - better exception
        try {
            IFile file = getFile(guid.toString());

            return FileUtils.UserFromFile(file);

        } catch (DataStorageException e) {
            throw new ManifestNotFoundException("Unable to get User");
        }
    }

    private Role getRoleFromGUID(IGUID guid) throws ManifestNotFoundException { // FIXME - better exception
        try {
            IFile file = getFile(guid.toString());

            return FileUtils.RoleFromFile(file);

        } catch (DataStorageException e) {
            throw new ManifestNotFoundException("Unable to get Role");
        }
    }
}

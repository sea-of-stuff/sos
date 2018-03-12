package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.ManifestBuilder;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.usro.RoleImpl;
import uk.ac.standrews.cs.sos.impl.usro.UserImpl;
import uk.ac.standrews.cs.sos.impl.usro.UsersRolesIndex;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.Internals.*;

/**
 * Service to manage Users and Roles.
 *
 * There is only one activeRole Role at a given time
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSUsersRolesService implements UsersRolesService {

    private UsersRolesIndex index;
    private LocalStorage localStorage;
    private ManifestsDataService manifestsDataService;

    public SOSUsersRolesService(LocalStorage localStorage, ManifestsDataService manifestsDataService) throws ServiceException {

        this.localStorage = localStorage;
        this.manifestsDataService = manifestsDataService;

        loadOrCreateCache();

        try {
            manageDefaultUser();
            manageDefaultRole();

        } catch (SignatureException | UserNotFoundException | ProtectionException | UserRolePersistException e) {
            throw new ServiceException(ServiceException.SERVICE.USRO, "Unable to manage default user/role correctly");
        }
    }

    @Override
    public Set<IGUID> getUsers() {

        return manifestsDataService.getManifests(ManifestType.USER);
    }

    @Override
    public Set<IGUID> getRoles() {

        return manifestsDataService.getManifests(ManifestType.ROLE);
    }

    @Override
    public void addUser(User user) throws UserRolePersistException {

        try {
            manifestsDataService.addManifest(user);
        } catch (ManifestPersistException e) {
            throw new UserRolePersistException(e);
        }
    }

    @Override
    public User getUser(IGUID userGUID) throws UserNotFoundException {

        try {
            return (User) manifestsDataService.getManifest(userGUID, NodeType.RMS);

        } catch (ManifestNotFoundException e) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public void addRole(Role role) throws UserRolePersistException {

        try {
            index.addRole(role);
            manifestsDataService.addManifest(role);

        } catch (ManifestPersistException e) {
            throw new UserRolePersistException(e);
        }
    }

    @Override
    public Role getRole(IGUID roleGUID) throws RoleNotFoundException {

        try {
            return (Role) manifestsDataService.getManifest(roleGUID, NodeType.RMS);

        } catch (ManifestNotFoundException e) {
            throw new RoleNotFoundException();
        }
    }

    @Override
    public Role getRole(ManifestBuilder manifestBuilder) {

        return manifestBuilder.getRole();
    }

    @Override
    public Set<IGUID> getRoles(IGUID userGUID) {

        return index.getRoles(userGUID);
    }

    @Override
    public void delete(IGUID guid) throws UserNotFoundException, RoleNotFoundException {

        boolean isUser = true;
        try {
            Manifest manifest = manifestsDataService.getManifest(guid);
            if (manifest.getType() == ManifestType.USER) {
                isUser = true;
            } else if (manifest.getType() == ManifestType.ROLE) {
                isUser = false;
            } else {
                throw new ManifestNotFoundException();
            }

            index.delete(guid);
            manifestsDataService.delete(guid);
        } catch (ManifestNotFoundException e) {

            if (isUser) {
                throw new UserNotFoundException();
            } else {
                throw new RoleNotFoundException();
            }
        }

    }

    @Override
    public Role activeRole() throws RoleNotFoundException {

        return index.activeRole();
    }

    @Override
    public void setActiveRole(Role role) throws UserRolePersistException {

        if (!role.hasPrivateKeys()) {
            throw new UserRolePersistException("You do not own the private keys to set the Role " + role.guid() + " as active");
        }

        index.setActiveRole(role);
    }

    @Override
    public User activeUser() throws UserNotFoundException {

        return index.activeUser();
    }

    @Override
    public void setActiveUser(User user) throws UserRolePersistException {

        if (!user.hasPrivateKeys()) {
            throw new UserRolePersistException("You do not own the private keys to set the User " + user.guid() + " as active");
        }

        index.setActiveUser(user);
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();

            IFile cacheFile = localStorage.createFile(cacheDir, USRO_INDEX_FILE);
            Persistence.persist(index, cacheFile);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the UserRoleService index");
        }
    }

    @Override
    public void shutdown() {
        index.clear();
    }

    private void loadOrCreateCache() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, USRO_INDEX_FILE);
            if (file.exists()) {
                index = UsersRolesIndex.load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the UserRoleService index");
        } catch (IgnoreException e) {
            SOS_LOG.log(LEVEL.WARN, "Ignore exception on usro index loading");
        }

        if (index == null) {
            index = new UsersRolesIndex();
        }
    }

    private void manageDefaultUser() throws SignatureException, UserRolePersistException {

        try {
            activeUser();

        } catch (UserNotFoundException e) {

            User defaultUser = new UserImpl(DEFAULT_USER_NAME);

            addUser(defaultUser);
            setActiveUser(defaultUser);
        }
    }

    private void manageDefaultRole() throws ProtectionException, SignatureException, UserNotFoundException, UserRolePersistException {

        try {
            activeRole();

        } catch (RoleNotFoundException e) {

            User defaultUser = activeUser();
            Role defaultRole = new RoleImpl(defaultUser, DEFAULT_ROLE_NAME);

            addRole(defaultRole);
            setActiveRole(defaultRole);
        }
    }


}

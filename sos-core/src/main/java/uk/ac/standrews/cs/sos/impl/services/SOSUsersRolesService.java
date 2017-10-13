package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
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
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.LinkedHashSet;
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
            throw new ServiceException("USRO - Unable to manage default user/role correctly");
        }
    }

    @Override
    public Set<User> getUsers() {

        Set<User> users = new LinkedHashSet<>();
        Set<IGUID> refs = manifestsDataService.getManifests(ManifestType.USER);
        for(IGUID ref:refs) {

            try {
                User user = (User) manifestsDataService.getManifest(ref);
                users.add(user);

            } catch (ManifestNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get User manifest from GUID " + ref.toMultiHash());
            }
        }


        return users;
    }

    @Override
    public Set<Role> getRoles() {

        Set<Role> roles = new LinkedHashSet<>();
        Set<IGUID> refs = manifestsDataService.getManifests(ManifestType.ROLE);
        for(IGUID ref:refs) {

            try {
                Role role = (Role) manifestsDataService.getManifest(ref);
                roles.add(role);

            } catch (ManifestNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get Role manifest from GUID " + ref.toMultiHash());
            }
        }


        return roles;

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
            return (User) manifestsDataService.getManifest(userGUID);

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
            return (Role) manifestsDataService.getManifest(roleGUID);

        } catch (ManifestNotFoundException e) {
            throw new RoleNotFoundException();
        }
    }

    @Override
    public Role getRole(ManifestBuilder manifestBuilder) throws RoleNotFoundException {

        return manifestBuilder.getRole();
    }

    @Override
    public Set<Role> getRoles(IGUID userGUID) {

        Set<Role> roles = new LinkedHashSet<>();
        Set<IGUID> roleRefs = index.getRoles(userGUID);
        for(IGUID ref:roleRefs) {

            try {
                Role role = (Role) manifestsDataService.getManifest(ref);
                roles.add(role);

            } catch (ManifestNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get Role manifest from GUID " + ref.toMultiHash());
            }
        }

        return roles;
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
            Persistence.Persist(index, cacheFile);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the UserRoleService index");
        }
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

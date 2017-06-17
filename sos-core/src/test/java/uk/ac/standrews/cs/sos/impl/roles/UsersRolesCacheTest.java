package uk.ac.standrews.cs.sos.impl.roles;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.ac.standrews.cs.sos.constants.Internals.USRO_CACHE_FILE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UsersRolesCacheTest extends SetUpTest {

    @Test
    public void constructorTest() {

        UsersRolesCache cache = new UsersRolesCache();
        assertNotNull(cache);
    }

    @Test
    public void activeTest() throws SignatureException, UserNotFoundException, ProtectionException, RoleNotFoundException {

        UsersRolesCache cache = new UsersRolesCache();

        User user = new UserImpl("TEST");
        cache.setActiveUser(user);
        assertEquals(cache.activeUser(), user);

        Role role = new RoleImpl(user, "TEST_ROLE");
        cache.setActiveRole(role);
        assertEquals(cache.activeRole(), role);
    }

    @Test
    public void saveLoadActiveUserTest() throws DataStorageException, IOException, SignatureException, ClassNotFoundException, UserNotFoundException {

        UsersRolesCache cache = new UsersRolesCache();
        User user = new UserImpl("TEST");
        cache.setActiveUser(user);

        IDirectory cacheDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cacheDir, USRO_CACHE_FILE);
        Persistence.Persist(cache, file);

        UsersRolesCache loadedCache = UsersRolesCache.load(file);
        assertNotNull(loadedCache);

        assertEquals(loadedCache.activeUser().toString(), user.toString());
    }

    @Test
    public void saveLoadActiveRoleTest() throws DataStorageException, IOException, SignatureException, ClassNotFoundException, ProtectionException, RoleNotFoundException {

        UsersRolesCache cache = new UsersRolesCache();
        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "TEST_ROLE");
        cache.setActiveRole(role);

        IDirectory cacheDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cacheDir, USRO_CACHE_FILE);
        Persistence.Persist(cache, file);

        UsersRolesCache loadedCache = UsersRolesCache.load(file);
        assertNotNull(loadedCache);

        assertEquals(loadedCache.activeRole().toString(), role.toString());
    }

    @Test
    public void saveLoadUsersTest() throws DataStorageException, IOException, SignatureException, ClassNotFoundException, UserNotFoundException {

        UsersRolesCache cache = new UsersRolesCache();
        User user = new UserImpl("TEST");
        User userOther = new UserImpl("TEST_OTHER");
        cache.addUser(user);
        cache.addUser(userOther);

        IDirectory cacheDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cacheDir, USRO_CACHE_FILE);
        Persistence.Persist(cache, file);

        UsersRolesCache loadedCache = UsersRolesCache.load(file);
        assertNotNull(loadedCache);

        assertEquals(loadedCache.getUsers().size(), 2);
        assertEquals(loadedCache.getUser(user.guid()).toString(), user.toString());
        assertEquals(loadedCache.getUser(userOther.guid()).toString(), userOther.toString());
    }


    @Test
    public void saveLoadRolesTest() throws DataStorageException, IOException, SignatureException, ClassNotFoundException, ProtectionException, RoleNotFoundException {

        UsersRolesCache cache = new UsersRolesCache();
        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "TEST_ROLE");
        Role roleOther = new RoleImpl(user, "TEST_ROLE_OTHER");
        cache.addRole(role);
        cache.addRole(roleOther);

        IDirectory cacheDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cacheDir, USRO_CACHE_FILE);
        Persistence.Persist(cache, file);

        UsersRolesCache loadedCache = UsersRolesCache.load(file);
        assertNotNull(loadedCache);

        assertEquals(loadedCache.getRoles().size(), 2);
        assertEquals(loadedCache.getRole(role.guid()).toString(), role.toString());
        assertEquals(loadedCache.getRole(roleOther.guid()).toString(), roleOther.toString());
    }
}
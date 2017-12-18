package uk.ac.standrews.cs.sos.impl.usro;

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
import static uk.ac.standrews.cs.sos.constants.Internals.USRO_INDEX_FILE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UsersRolesIndexTest extends SetUpTest {

    @Test
    public void constructorTest() {

        UsersRolesIndex cache = new UsersRolesIndex();
        assertNotNull(cache);
    }

    @Test
    public void activeTest() throws SignatureException, UserNotFoundException, ProtectionException, RoleNotFoundException {

        UsersRolesIndex cache = new UsersRolesIndex();

        User user = new UserImpl("TEST");
        cache.setActiveUser(user);
        assertEquals(cache.activeUser(), user);

        Role role = new RoleImpl(user, "TEST_ROLE");
        cache.setActiveRole(role);
        assertEquals(cache.activeRole(), role);
    }

    @Test
    public void saveLoadActiveUserTest() throws DataStorageException, IOException, SignatureException, ClassNotFoundException, UserNotFoundException {

        UsersRolesIndex cache = new UsersRolesIndex();
        User user = new UserImpl("TEST");
        cache.setActiveUser(user);

        IDirectory cacheDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cacheDir, USRO_INDEX_FILE);
        Persistence.persist(cache, file);

        UsersRolesIndex loadedCache = UsersRolesIndex.load(file);
        assertNotNull(loadedCache);

        assertEquals(loadedCache.activeUser().toString(), user.toString());
    }

    @Test
    public void saveLoadActiveRoleTest() throws DataStorageException, IOException, SignatureException, ClassNotFoundException, ProtectionException, RoleNotFoundException {

        UsersRolesIndex cache = new UsersRolesIndex();
        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "TEST_ROLE");
        cache.setActiveRole(role);

        IDirectory cacheDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cacheDir, USRO_INDEX_FILE);
        Persistence.persist(cache, file);

        UsersRolesIndex loadedCache = UsersRolesIndex.load(file);
        assertNotNull(loadedCache);

        assertEquals(loadedCache.activeRole().toString(), role.toString());
    }

    @Test
    public void saveLoadRolesTest() throws DataStorageException, IOException, SignatureException, ClassNotFoundException, ProtectionException, RoleNotFoundException {

        UsersRolesIndex cache = new UsersRolesIndex();
        User user = new UserImpl("TEST");
        Role role = new RoleImpl(user, "TEST_ROLE");
        Role roleOther = new RoleImpl(user, "TEST_ROLE_OTHER");
        cache.addRole(role);
        cache.addRole(roleOther);

        IDirectory cacheDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cacheDir, USRO_INDEX_FILE);
        Persistence.persist(cache, file);

        UsersRolesIndex loadedCache = UsersRolesIndex.load(file);
        assertNotNull(loadedCache);

        assertEquals(loadedCache.getRoles(user.guid()).size(), 2);
    }
}
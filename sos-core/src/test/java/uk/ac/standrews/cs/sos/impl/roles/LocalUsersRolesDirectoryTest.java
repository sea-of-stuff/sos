package uk.ac.standrews.cs.sos.impl.roles;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalUsersRolesDirectoryTest extends CommonTest {

    private LocalStorage storage;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        storage = new LocalStorage(stor);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        storage.destroy();
    }

    @Test
    public void addUserTest() throws Exception {
        LocalUsersRolesDirectory usroDir = new LocalUsersRolesDirectory(storage);

        User user = new UserImpl("test");
        usroDir.addUser(user);

        User retrieved = usroDir.getUser(user.guid());
        assertEquals(retrieved.guid(), user.guid());
        assertEquals(retrieved.getName(), user.getName());
    }

    @Test
    public void addRoleTest() throws Exception {
        LocalUsersRolesDirectory usroDir = new LocalUsersRolesDirectory(storage);
        
        User user = new UserImpl("test");

        Role role = new RoleImpl(user, "test_role");
        usroDir.addRole(role);

        Role retrieved = usroDir.getRole(role.guid());
        assertEquals(retrieved.guid(), role.guid());
        assertEquals(retrieved.getName(), role.getName());
    }

    @Test
    public void activeUserTest() throws SignatureException, UserNotFoundException, UserRolePersistException {
        LocalUsersRolesDirectory usroDir = new LocalUsersRolesDirectory(storage);

        User user = new UserImpl("test");

        usroDir.setActiveUser(user);
        User activeUser = usroDir.activeUser();

        assertEquals(activeUser.guid(), user.guid());
        assertEquals(activeUser.getName(), user.getName());
    }

    @Test
    public void activeRoleTest() throws SignatureException, UserRolePersistException, ProtectionException, RoleNotFoundException {
        LocalUsersRolesDirectory usroDir = new LocalUsersRolesDirectory(storage);

        User user = new UserImpl("test");

        Role role = new RoleImpl(user, "test_role");

        usroDir.setActiveRole(role);
        Role activeRole = usroDir.activeRole();

        assertEquals(activeRole.guid(), role.guid());
        assertEquals(activeRole.getName(), role.getName());
    }

}

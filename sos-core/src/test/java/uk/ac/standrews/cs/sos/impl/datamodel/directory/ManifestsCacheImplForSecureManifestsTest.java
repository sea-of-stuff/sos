package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.usro.RoleImpl;
import uk.ac.standrews.cs.sos.impl.usro.UserImpl;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureAtom;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImplForSecureManifestsTest extends CommonTest {

    private LocalStorage localStorage;
    private User user;
    private Role role;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/config_setup.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        new SOS_LOG(GUIDFactory.generateRandomGUID());

        try {
            String root = System.getProperty("user.home") + "/sos/";

            CastoreBuilder castoreBuilder = new CastoreBuilder()
                    .setType(CastoreType.LOCAL)
                    .setRoot(root);
            IStorage stor = CastoreFactory.createStorage(castoreBuilder);
            localStorage = new LocalStorage(stor);

        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        user = new UserImpl("TEST");
        role = new RoleImpl(user, "ROLE_TEST");
    }

    @AfterMethod
    public void tearDown() throws DataStorageException {
        localStorage.destroy();
    }

    @Test
    public void basicTest() throws ManifestPersistException, ManifestNotFoundException, URISyntaxException, GUIDGenerationException, ManifestNotMadeException {

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);

        HashMap<IGUID, String> rolesToKeys = new HashMap<>();
        rolesToKeys.put(role.guid(), "ENCRYPTED_KEY");

        SecureAtom secureAtomManifest = ManifestFactory.createSecureAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles, rolesToKeys);


        ManifestsCache cache = new ManifestsCacheImpl();
        cache.addManifest(secureAtomManifest);

        assertEquals(cache.findManifest(secureAtomManifest.guid()), secureAtomManifest);
    }

    @Test
    public void secureAtomUpdateTest() throws ManifestPersistException, ManifestNotFoundException, URISyntaxException, GUIDGenerationException, ManifestNotMadeException, ProtectionException, SignatureException {

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);

        HashMap<IGUID, String> rolesToKeys = new HashMap<>();
        rolesToKeys.put(role.guid(), "ENCRYPTED_KEY");

        SecureAtom secureAtomManifest = ManifestFactory.createSecureAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles, rolesToKeys);

        ManifestsCache cache = new ManifestsCacheImpl();
        cache.addManifest(secureAtomManifest);
        assertEquals(cache.findManifest(secureAtomManifest.guid()), secureAtomManifest);

        Role otherRole = new RoleImpl(user, "OTHER_ROLE");
        secureAtomManifest.addKeyRole(otherRole.guid(), "OTHER_ENCRYPTED_KEY");

        cache.addManifest(secureAtomManifest);
        assertEquals(cache.findManifest(secureAtomManifest.guid()), secureAtomManifest);
        assertEquals(((SecureAtom) cache.findManifest(secureAtomManifest.guid())).keysRoles().size(), 2);
    }
}
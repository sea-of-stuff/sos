package uk.ac.standrews.cs.sos.impl.manifests;

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
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static uk.ac.standrews.cs.sos.constants.Hashes.TEST_HTTP_BIN_CONTENT;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifestTest extends CommonTest {

    private LocalStorage localStorage;
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

        User user = new UserImpl("TEST");
        role = new RoleImpl(user, "ROLE_TEST");
    }

    @AfterMethod
    public void tearDown() throws DataStorageException {
        localStorage.destroy();
    }

    //Create a SecureAtomManifest. The data is not encrypted when creating the manifest, but by the Storage service.
    @Test
    public void basicTest() throws DataStorageException, StorageException, URISyntaxException, GUIDGenerationException, ManifestNotMadeException, IOException, ProtectionException {

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);

        HashMap<IGUID, String> rolesToKeys = new HashMap<>();
        rolesToKeys.put(role.guid(), "ENCRYPTED_KEY");

        SecureAtom secureAtomManifest = ManifestFactory.createSecureAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles, rolesToKeys);
        assertEquals(secureAtomManifest.getType(), ManifestType.ATOM_PROTECTED);
        assertEquals(secureAtomManifest.getData().toString(), TEST_HTTP_BIN_CONTENT);
    }

    @Test
    public void deserializeTest() throws IOException, GUIDGenerationException {

        String testSecureAtomJson = "" +
                "{\n" +
                "  \"Type\" : \"AtomP\",\n" +
                "  \"GUID\" : \"SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0\",\n" +
                "  \"Locations\" : [ {\n" +
                "    \"Type\" : \"provenance\",\n" +
                "    \"Location\" : \"http://httpbin.org/range/10\"\n" +
                "  } ],\n" +
                "  \"Keys\" : [ {\n" +
                "    \"Role\" : \"SHA256_16_4f168a2da5be0120d896630b10cf111cec6f6a5e58fa683321d5ef08c6612a88\",\n" +
                "    \"Key\" : \"ENCRYPTED_KEY\"\n" +
                "  } ]\n" +
                "}";

        SecureAtom secureAtomManifest = JSONHelper.JsonObjMapper().readValue(testSecureAtomJson, SecureAtomManifest.class);
        assertEquals(secureAtomManifest.getType(), ManifestType.ATOM_PROTECTED);
        assertEquals(secureAtomManifest.guid(), GUIDFactory.recreateGUID("SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0"));

        assertEquals(secureAtomManifest.getLocations().size(), 1);
        LocationBundle bundle = secureAtomManifest.getLocations().iterator().next();
        assertEquals(bundle.getType(), BundleTypes.PROVENANCE);
        assertEquals(bundle.getLocation().toString(), "http://httpbin.org/range/10");

        assertEquals(secureAtomManifest.keysRoles().size(), 1);
        assertTrue(secureAtomManifest.keysRoles().containsKey(GUIDFactory.recreateGUID("SHA256_16_4f168a2da5be0120d896630b10cf111cec6f6a5e58fa683321d5ef08c6612a88")));
        assertEquals(secureAtomManifest.keysRoles().get(GUIDFactory.recreateGUID("SHA256_16_4f168a2da5be0120d896630b10cf111cec6f6a5e58fa683321d5ef08c6612a88")), "ENCRYPTED_KEY");
    }

}
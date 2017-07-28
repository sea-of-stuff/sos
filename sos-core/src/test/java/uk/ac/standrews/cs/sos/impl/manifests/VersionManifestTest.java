package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.JsonNode;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.UserRoleUtils;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifestTest extends CommonTest {

    private static final String EXPECTED_JSON_BASIC_VERSION =
            "{\"Type\":\"Version\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_VERSION =
            "{\"Type\":\"Version\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_PREVIOUS_VERSION =
            "{\"Type\":\"Version\"," +
                    "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_AND_PREVIOUS_VERSION =
            "{\"Type\":\"Version\"," +
                    "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    @Test
    public void testBasicConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(null, guid, null, null, roleMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_BASIC_VERSION, versionManifest.toString(), false);
    }

    @Test
    public void testMetadataConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, metadataStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(null, guid, null, metadataGUID, roleMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_VERSION, versionManifest.toString(), false);
    }

    @Test
    public void testPreviousVersionConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(invariantGUID, guid, previous, null, roleMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_PREVIOUS_VERSION, versionManifest.toString(), false);
    }

    @Test
    public void testMetadataAndPreviousVersionConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, metadataStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(invariantGUID, guid, previous, metadataGUID, roleMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_AND_PREVIOUS_VERSION, versionManifest.toString(), false);
    }

    @Test
    public void testGetters() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, metadataStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(invariantGUID, guid, previous, metadataGUID, roleMocked);

        assertEquals(versionManifest.getContentGUID(), guid);
        assertEquals(versionManifest.getInvariantGUID(), invariantGUID);
        assertEquals(versionManifest.getMetadata(), metadataGUID);
        assertEquals(versionManifest.getPreviousVersions(), previous);
    }
}
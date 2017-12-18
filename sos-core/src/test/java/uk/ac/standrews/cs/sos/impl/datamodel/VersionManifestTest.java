package uk.ac.standrews.cs.sos.impl.datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
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
import static org.testng.Assert.assertNotEquals;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifestTest extends CommonTest {

    private static final String EXPECTED_JSON_BASIC_VERSION =
            "{\"type\":\"Version\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_VERSION =
            "{\"type\":\"Version\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_PREVIOUS_VERSION =
            "{\"type\":\"Version\"," +
                    "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_AND_PREVIOUS_VERSION =
            "{\"type\":\"Version\"," +
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
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(null, guid, null, null, roleMocked);

        JsonNode node = JSONHelper.jsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_BASIC_VERSION, versionManifest.toString(), false);

        assertNotEquals(versionManifest.size(), -1);
        assertEquals(versionManifest.size(), 418);
    }

    @Test
    public void testMetadataConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, metadataStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(null, guid, null, metadataGUID, roleMocked);

        JsonNode node = JSONHelper.jsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_VERSION, versionManifest.toString(), false);
    }

    @Test
    public void testPreviousVersionConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(invariantGUID, guid, previous, null, roleMocked);

        JsonNode node = JSONHelper.jsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_PREVIOUS_VERSION, versionManifest.toString(), false);
    }

    @Test
    public void testMetadataAndPreviousVersionConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, metadataStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(invariantGUID, guid, previous, metadataGUID, roleMocked);

        JsonNode node = JSONHelper.jsonObjMapper().readTree(versionManifest.toString());
        Assert.assertTrue(node.has(JSONConstants.KEY_GUID));
        Assert.assertTrue(node.has(JSONConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_AND_PREVIOUS_VERSION, versionManifest.toString(), false);
    }

    @Test
    public void testGetters() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, metadataStreamFake);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        VersionManifest versionManifest = new VersionManifest(invariantGUID, guid, previous, metadataGUID, roleMocked);

        assertEquals(versionManifest.content(), guid);
        assertEquals(versionManifest.invariant(), invariantGUID);
        assertEquals(versionManifest.getMetadata(), metadataGUID);
        assertEquals(versionManifest.previous(), previous);
    }
}
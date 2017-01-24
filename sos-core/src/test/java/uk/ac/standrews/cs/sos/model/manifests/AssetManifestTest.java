package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.databind.JsonNode;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifestTest extends CommonTest {

    private static final String EXPECTED_JSON_BASIC_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_PREVIOUS_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_AND_PREVIOUS_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    @Test
    public void testBasicAssetConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(null, guid, null, null, identityMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(assetManifest.toString());
        Assert.assertTrue(node.has(ManifestConstants.KEY_VERSION));
        Assert.assertTrue(node.has(ManifestConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_BASIC_ASSET, assetManifest.toString(), false);
    }

    @Test
    public void testMetadataAssetConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(metadataStreamFake);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(null, guid, null, metadataGUID, identityMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(assetManifest.toString());
        Assert.assertTrue(node.has(ManifestConstants.KEY_VERSION));
        Assert.assertTrue(node.has(ManifestConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_ASSET, assetManifest.toString(), false);
    }

    @Test
    public void testPreviousAssetConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(invariantGUID, guid, previous, null, identityMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(assetManifest.toString());
        Assert.assertTrue(node.has(ManifestConstants.KEY_VERSION));
        Assert.assertTrue(node.has(ManifestConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_PREVIOUS_ASSET, assetManifest.toString(), false);
    }

    @Test
    public void testMetadataAndPreviousAssetConstructor() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(metadataStreamFake);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(invariantGUID, guid, previous, metadataGUID, identityMocked);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(assetManifest.toString());
        Assert.assertTrue(node.has(ManifestConstants.KEY_VERSION));
        Assert.assertTrue(node.has(ManifestConstants.KEY_INVARIANT));

        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_AND_PREVIOUS_ASSET, assetManifest.toString(), false);
    }

    @Test
    public void testGetters() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        InputStream invariantStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID invariantGUID = GUIDFactory.generateGUID(invariantStreamFake);

        InputStream previousStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID previousGUID = GUIDFactory.generateGUID(previousStreamFake);
        Set<IGUID> previous = new LinkedHashSet<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID metadataGUID = GUIDFactory.generateGUID(metadataStreamFake);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(invariantGUID, guid, previous, metadataGUID, identityMocked);

        assertEquals(assetManifest.getContentGUID(), guid);
        assertEquals(assetManifest.getInvariantGUID(), invariantGUID);
        assertEquals(assetManifest.getMetadata(), metadataGUID);
        assertEquals(assetManifest.getPreviousVersions(), previous);
    }
}
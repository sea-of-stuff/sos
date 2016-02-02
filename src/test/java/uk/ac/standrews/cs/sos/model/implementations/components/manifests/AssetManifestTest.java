package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import com.google.gson.JsonObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.IO.utils.StreamsUtils;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifestTest {

    private static final String EXPECTED_JSON_BASIC_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Content\":" +
                    "{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Metadata\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"Content\":" +
                    "{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_PREVIOUS_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"Content\":" +
                    "{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    private static final String EXPECTED_JSON_METADATA_AND_PREVIOUS_ASSET =
            "{\"Type\":\"Asset\"," +
                    "\"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Metadata\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
                    "\"Content\":" +
                    "{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}}";

    @Test
    public void testBasicAssetConstructor() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);
        Content cat = new Content("cat", guid);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(null, cat, null, null, identityMocked);

        JsonObject gson = assetManifest.toJSON();
        assertNotNull(gson.get("Version"));
        assertNotNull(gson.get("Invariant"));
        JSONAssert.assertEquals(EXPECTED_JSON_BASIC_ASSET, gson.toString(), false);
    }

    @Test
    public void testMetadataAssetConstructor() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);
        Content cat = new Content("cat", guid);

        InputStream metadataStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 metadataGUID = new GUIDsha1(metadataStreamFake);
        Collection<GUID> metadata = new ArrayList<>();
        metadata.add(metadataGUID);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(null, cat, null, metadata, identityMocked);

        JsonObject gson = assetManifest.toJSON();
        assertNotNull(gson.get("Version"));
        assertNotNull(gson.get("Invariant"));
        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_ASSET, gson.toString(), false);
    }

    @Test
    public void testPreviousAssetConstructor() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);
        Content cat = new Content("cat", guid);

        InputStream invariantStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 invariantGUID = new GUIDsha1(invariantStreamFake);

        InputStream previousStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 previousGUID = new GUIDsha1(previousStreamFake);
        Collection<GUID> previous = new ArrayList<>();
        previous.add(previousGUID);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(invariantGUID, cat, previous, null, identityMocked);

        JsonObject gson = assetManifest.toJSON();
        assertNotNull(gson.get("Version"));
        JSONAssert.assertEquals(EXPECTED_JSON_PREVIOUS_ASSET, gson.toString(), false);
    }

    @Test
    public void testMetadataAndPreviousAssetConstructor() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);
        Content cat = new Content("cat", guid);

        InputStream invariantStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 invariantGUID = new GUIDsha1(invariantStreamFake);

        InputStream previousStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 previousGUID = new GUIDsha1(previousStreamFake);
        Collection<GUID> previous = new ArrayList<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 metadataGUID = new GUIDsha1(metadataStreamFake);
        Collection<GUID> metadata = new ArrayList<>();
        metadata.add(metadataGUID);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(invariantGUID, cat, previous, metadata, identityMocked);

        JsonObject gson = assetManifest.toJSON();
        assertNotNull(gson.get("Version"));
        JSONAssert.assertEquals(EXPECTED_JSON_METADATA_AND_PREVIOUS_ASSET, gson.toString(), false);
    }

    @Test
    public void testGetters() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);
        Content cat = new Content("cat", guid);

        InputStream invariantStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 invariantGUID = new GUIDsha1(invariantStreamFake);

        InputStream previousStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 previousGUID = new GUIDsha1(previousStreamFake);
        Collection<GUID> previous = new ArrayList<>();
        previous.add(previousGUID);

        InputStream metadataStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 metadataGUID = new GUIDsha1(metadataStreamFake);
        Collection<GUID> metadata = new ArrayList<>();
        metadata.add(metadataGUID);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(invariantGUID, cat, previous, metadata, identityMocked);

        assertEquals(assetManifest.getContentGUID(), guid);
        assertEquals(assetManifest.getContent(), cat);
        assertEquals(assetManifest.getInvariantGUID(), invariantGUID);
        assertEquals(assetManifest.getMetadata(), metadata);
        assertEquals(assetManifest.getPreviousManifests(), previous);
    }
}
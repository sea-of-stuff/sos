package sos.model.implementations.components.manifests;

import IO.utils.StreamsUtils;
import com.google.gson.JsonObject;
import constants.Hashes;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.interfaces.identity.Identity;

import java.io.InputStream;

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
                    "\"Signature\":\"000001\"," +
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
        when(identityMocked.encrypt(any(String.class))).thenReturn(fakedSignature);

        AssetManifest assetManifest = new AssetManifest(cat, identityMocked);
        assertEquals(assetManifest.getContent(), cat);

        System.out.print(assetManifest.toString());
        JsonObject gson = assetManifest.toJSON();
        assertNotNull(gson.get("Version"));
        assertNotNull(gson.get("Invariant"));
        JSONAssert.assertEquals(EXPECTED_JSON_BASIC_ASSET, gson.toString(), false);
    }
}
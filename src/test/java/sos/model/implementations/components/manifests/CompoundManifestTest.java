package sos.model.implementations.components.manifests;

import IO.utils.StreamsUtils;
import constants.Hashes;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.interfaces.identity.Identity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestTest {

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ContentGUID\":\"5ddcc2228d3d80966b29f709774c2d5ee15a99a0\"," +
                    "\"Signature\":\"000001\"," +
                    "\"Contents\":" +
                    "[{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                            "}]}";

    private static final String EXPECTED_JSON_NO_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ContentGUID\":\"97d170e1550eee4afc0af065b78cda302a97674c\"," +
                    "\"Signature\":\"000001\"," +
                    "\"Contents\":" +
                    "[]}";

    @Test
    public void testToStringContents() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content cat = new Content("cat", guid);
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.encrypt(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(contents, identityMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENTS, compoundManifest.toString(), true);
    }

    @Test
    public void testToStringNoContents() throws Exception {

        Collection<Content> contents = Collections.EMPTY_LIST;

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.encrypt(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(contents, identityMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_NO_CONTENTS, compoundManifest.toString(), true);
    }
}
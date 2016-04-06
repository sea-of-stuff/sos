package uk.ac.standrews.cs.sos.model.manifests;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.utils.GUIDFactory;
import uk.ac.standrews.cs.utils.IGUID;
import uk.ac.standrews.cs.utils.StreamsUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestTest extends SetUpTest {

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ContentGUID\":\"5ddcc2228d3d80966b29f709774c2d5ee15a99a0\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Compound_Type\":\"DATA\"," +
                    "\"Content\":" +
                    "[{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                            "}]}";

    private static final String EXPECTED_JSON_NO_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ContentGUID\":\"97d170e1550eee4afc0af065b78cda302a97674c\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Compound_Type\":\"DATA\"," +
                    "\"Content\":" +
                    "[]}";

    @Test
    public void testToStringContents() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENTS, compoundManifest.toJSON().toString(), true);
    }

    @Test
    public void testGetContents() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        assertNotNull(compoundManifest.getContents());
        assertEquals(compoundManifest.getContents().size(), 1);
        Iterator<Content> iterator = compoundManifest.getContents().iterator();
        assertEquals(iterator.next(), cat);
        assertNotNull(compoundManifest.getContentGUID());
    }

    @Test
    public void testGetNoContents() throws Exception {
        Collection<Content> contents = Collections.EMPTY_LIST;

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        assertNotNull(compoundManifest.getContents());
        assertEquals(compoundManifest.getContents().size(), 0);
    }

    @Test
    public void testToStringNoContents() throws Exception {
        Collection<Content> contents = Collections.EMPTY_LIST;

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_NO_CONTENTS, compoundManifest.toJSON().toString(), true);
    }

    @Test
    public void testIsValidManifest() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        assertTrue(compoundManifest.isValid());
    }

    @Test
    public void testIsNotValidManifest() throws Exception {
        Collection<Content> contents = new ArrayList<>();

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        assertTrue(compoundManifest.isValid());
    }

    @Test (expectedExceptions = ManifestNotMadeException.class)
    public void testIsNoCompoundTypeNotValidManifest() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(null, contents, identityMocked);
    }
}
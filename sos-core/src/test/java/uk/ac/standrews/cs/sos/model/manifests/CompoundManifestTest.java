package uk.ac.standrews.cs.sos.model.manifests;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestTest extends CommonTest {

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ContentGUID\":\"44ebfc76e2671daf7f1e1b02c9538e5fe1e44995\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Compound_Type\":\"DATA\"," +
                    "\"Content\":" +
                    "[{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                            "}]}";

    private static final String EXPECTED_JSON_NO_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ContentGUID\":\"32096c2e0eff33d844ee6d675407ace18289357d\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Compound_Type\":\"DATA\"," +
                    "\"Content\":" +
                    "[]}";

    @Test
    public void testToStringContents() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENTS, compoundManifest.toString(), true);
    }

    @Test
    public void testGetContents() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
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
        Set<Content> contents = Collections.EMPTY_SET;

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        assertNotNull(compoundManifest.getContents());
        assertEquals(compoundManifest.getContents().size(), 0);
    }

    @Test
    public void testToStringNoContents() throws Exception {
        Set<Content> contents = Collections.EMPTY_SET;

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_NO_CONTENTS, compoundManifest.toString(), true);
    }

    @Test
    public void testIsValidManifest() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        assertTrue(compoundManifest.isValid());
    }

    @Test
    public void testIsNotValidManifest() throws Exception {
        Set<Content> contents = new LinkedHashSet<>();

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, identityMocked);

        assertTrue(compoundManifest.isValid());
    }

}
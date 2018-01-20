package uk.ac.standrews.cs.sos.impl.datamodel;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.model.CompoundType;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.UserRoleUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestTest extends CommonTest {

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"type\":\"Compound\"," +
                    "\"guid\":\"SHA256_16_95cdd566156fd9891cd849a582abbc4ed37b0e5c64a099dbf0093b8a3d2d6a79\"," +
                    "\"signature\":\"AAAB\"," +
                    "\"signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"compound_type\":\"DATA\"," +
                    "\"contents\":" +
                    "[{" +
                    "\"label\":\"cat\"," +
                    "\"guid\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                            "}]}";

    private static final String EXPECTED_JSON_NO_CONTENTS =
            "{\"type\":\"Compound\"," +
                    "\"guid\":\"SHA256_16_c3dbaa4197ea2aa8012e70fd805d9cc6c450cc78e454b2a6a643f935a3454c76\"," +
                    "\"signature\":\"AAAB\"," +
                    "\"signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"compound_type\":\"DATA\"," +
                    "\"contents\":" +
                    "[]}";

    @Test
    public void testToStringContents() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        Content cat = new ContentImpl("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, roleMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENTS, compoundManifest.toString(), true);

        assertNotEquals(compoundManifest.size(), -1);
        assertEquals(compoundManifest.size(), 394);
    }

    @Test
    public void testGetContents() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        Content cat = new ContentImpl("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, roleMocked);

        assertNotNull(compoundManifest.getContents());
        assertEquals(compoundManifest.getContents().size(), 1);
        Iterator<Content> iterator = compoundManifest.getContents().iterator();
        assertEquals(iterator.next(), cat);
        assertNotNull(compoundManifest.guid());
    }

    @Test
    public void testGetNoContents() throws Exception {
        Set<Content> contents = Collections.EMPTY_SET;

        Role roleMocked = UserRoleUtils.BareRoleMock();
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, roleMocked);

        assertNotNull(compoundManifest.getContents());
        assertEquals(compoundManifest.getContents().size(), 0);
    }

    @Test
    public void testToStringNoContents() throws Exception {
        Set<Content> contents = Collections.EMPTY_SET;

        Role roleMocked = UserRoleUtils.BareRoleMock();
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, roleMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_NO_CONTENTS, compoundManifest.toString(), true);
    }

    @Test
    public void testIsValidManifest() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        Content cat = new ContentImpl("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, roleMocked);

        assertTrue(compoundManifest.isValid());
    }

    @Test
    public void testIsNotValidManifest() throws Exception {
        Set<Content> contents = new LinkedHashSet<>();

        Role roleMocked = UserRoleUtils.BareRoleMock();
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, roleMocked);

        assertTrue(compoundManifest.isValid());
    }

}
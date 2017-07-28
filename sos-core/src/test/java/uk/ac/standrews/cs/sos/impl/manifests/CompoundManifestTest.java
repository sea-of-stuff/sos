package uk.ac.standrews.cs.sos.impl.manifests;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.ALGORITHM;
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

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestTest extends CommonTest {

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"GUID\":\"SHA256_16_751ca025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Compound_Type\":\"DATA\"," +
                    "\"Content\":" +
                    "[{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                            "}]}";

    private static final String EXPECTED_JSON_NO_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"GUID\":\"SHA256_16_6b23c0d5f35d1b11f9b683f0b0a617355deb11277d91ae091d399c655b87940d\"," +
                    "\"Signature\":\"AAAB\"," +
                    "\"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
                    "\"Compound_Type\":\"DATA\"," +
                    "\"Content\":" +
                    "[]}";

    @Test
    public void testToStringContents() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

        Content cat = new ContentImpl("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        CompoundManifest compoundManifest = new CompoundManifest(CompoundType.DATA, contents, roleMocked);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENTS, compoundManifest.toString(), true);
    }

    @Test
    public void testGetContents() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

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
        IGUID guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStreamFake);

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
package model.implementations.components.manifests;

import IO.utils.StreamsUtils;
import model.implementations.utils.Content;
import model.implementations.utils.GUIDsha1;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestTest {

    private static final String TEST_STRING = "TEST";
    private static final String TEST_STRING_HASHED = "984816fd329622876e14907634264e6f332e9fb3";

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"Contents\":" +
                    "[{" +
                    "\"Type\":\"label\"," +
                    "\"Value\":\"cat\"," +
                    "\"GUID\":\""+TEST_STRING_HASHED+"\"" +
                            "}]}";

    private static final String EXPECTED_JSON_NO_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"Contents\":" +
                    "[]}";

    @Test
    public void testToStringContents() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content cat = new Content("label", "cat", guid);
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest compoundManifest = new CompoundManifest(contents);

        assertEquals(EXPECTED_JSON_CONTENTS, compoundManifest.toString());
    }

    @Test
    public void testToStringNoContents() throws Exception {

        Collection<Content> contents = Collections.EMPTY_LIST;

        CompoundManifest compoundManifest = new CompoundManifest(contents);

        assertEquals(EXPECTED_JSON_NO_CONTENTS, compoundManifest.toString());
    }
}
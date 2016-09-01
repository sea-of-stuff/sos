package uk.ac.standrews.cs.sos.model.utils;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.impl.keys.KeyImpl;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContentTest {

    private static final String EXPECTED_JSON_CONTENT_GUID = "{\"GUID\":\""+
            Hashes.TEST_STRING_HASHED+"\"}";
    private static final String EXPECTED_JSON_CONTENT_TYPE_VAL =
            "{" +
                    "\"Label\":\"cat\"," +
                    "\"GUID\":\""+Hashes.TEST_STRING_HASHED+"\"" +
                    "}";

    @Test
    public void testConstructorAndGetter() {
        IGUID mockedGUID = mock(KeyImpl.class);
        Content content = new Content(mockedGUID);

        assertEquals(mockedGUID, content.getGUID());
    }

    @Test
    public void testOtherConstructorAndGetters() {
        IGUID mockedGUID = mock(KeyImpl.class);
        Content content = new Content("testlabel", mockedGUID);

        assertEquals("testlabel", content.getLabel());
        assertEquals(mockedGUID, content.getGUID());
    }

    @Test
    public void testToStringGUID() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content content = new Content(guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString(), true);
    }

    @Test
    public void testToStringWithLabel() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content content = new Content("cat", guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_TYPE_VAL, content.toString(), true);
    }

    @Test
    public void testToStringWithEmptyLabel() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content content = new Content("", guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString(), true);
    }

    @Test
    public void testToStringWithNullLabel() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content content = new Content(null, guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString(), true);
    }

}
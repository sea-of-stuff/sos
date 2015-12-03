package model.implementations.utils;

import IO.utils.StreamsUtils;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContentTest {

    private static final String TEST_STRING = "TEST";
    private static final String TEST_STRING_HASHED = "984816fd329622876e14907634264e6f332e9fb3";

    private static final String EXPECTED_JSON_CONTENT_GUID = "{\"GUID\":\""+TEST_STRING_HASHED+"\"}";
    private static final String EXPECTED_JSON_CONTENT_TYPE_VAL =
            "{" +
                    "\"Type\":\"label\"," +
                    "\"Value\":\"cat\"," +
                    "\"GUID\":\""+TEST_STRING_HASHED+"\"" +
                    "}";

    @Test
    public void testToStringGUID() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content(guid);

        assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString());
    }

    @Test
    public void testToStringWithTypeVal() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content("label", "cat", guid);

        assertEquals(EXPECTED_JSON_CONTENT_TYPE_VAL, content.toString());
    }

    @Test
    public void testToStringWithEmptyType() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content("", "cat", guid);

        assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString());
    }

    @Test
    public void testToStringWithEmptyVal() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content("label", "", guid);

        assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString());
    }

    @Test
    public void testToStringWithEmptyTypeVal() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content("", "", guid);

        assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString());
    }

    @Test
    public void testToStringWithNullType() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content(null, "cat", guid);

        assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString());
    }

    @Test
    public void testToStringWithNullVal() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content("label", null, guid);

        assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString());
    }

    @Test
    public void testToStringWithNullTypeVal() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content content = new Content(null, null, guid);

        assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString());
    }
}
package uk.ac.standrews.cs.sos.impl.utils;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.impl.keys.KeyImpl;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContentImplTest extends CommonTest {

    private static final String EXPECTED_JSON_CONTENT_GUID = "{\"guid\":\""+
            Hashes.TEST_STRING_HASHED+"\"}";
    private static final String EXPECTED_JSON_CONTENT_TYPE_VAL =
            "{" +
                    "\"label\":\"cat\"," +
                    "\"guid\":\""+Hashes.TEST_STRING_HASHED+"\"" +
                    "}";

    @Test
    public void testConstructorAndGetter() {
        IGUID mockedGUID = mock(KeyImpl.class);
        ContentImpl content = new ContentImpl(mockedGUID);

        assertEquals(mockedGUID, content.getGUID());
    }

    @Test
    public void testOtherConstructorAndGetters() {
        IGUID mockedGUID = mock(KeyImpl.class);
        ContentImpl content = new ContentImpl("testlabel", mockedGUID);

        assertEquals("testlabel", content.getLabel());
        assertEquals(mockedGUID, content.getGUID());
    }

    @Test
    public void testToStringGUID() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        ContentImpl content = new ContentImpl(guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString(), true);
    }

    @Test
    public void testToStringWithLabel() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        ContentImpl content = new ContentImpl("cat", guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_TYPE_VAL, content.toString(), true);
    }

    @Test
    public void testToStringWithEmptyLabel() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        ContentImpl content = new ContentImpl("", guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString(), true);
    }

    @Test
    public void testToStringWithNullLabel() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        ContentImpl content = new ContentImpl(null, guid);

        JSONAssert.assertEquals(EXPECTED_JSON_CONTENT_GUID, content.toString(), true);
    }

}
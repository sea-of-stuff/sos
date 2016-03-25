package uk.ac.standrews.cs.sos.model.utils;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.utils.GUID;
import uk.ac.standrews.cs.utils.GUIDsha1;
import uk.ac.standrews.cs.utils.StreamsUtils;

import java.io.InputStream;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GUIDTest extends SetUpTest {

    @Test
    public void testGetHashHexAndSize() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUID guid = new GUIDsha1(inputStreamFake);
        assertEquals(Hashes.TEST_STRING_HASHED, guid.toString());
    }

    @Test (expectedExceptions = GuidGenerationException.class)
    public void testNullStream() throws Exception {
        InputStream stream = null;
        new GUIDsha1(stream);
    }

    @Test
    public void testGenerateGUID() throws Exception {
        GUID guid = GUID.generateGUID(Hashes.TEST_STRING);
        assertEquals(Hashes.TEST_STRING_HASHED, guid.toString());
    }

    @Test (expectedExceptions = GuidGenerationException.class)
    public void testGenerateGUIDNullString() throws Exception {
        String string = null;
        GUID.generateGUID(string);
    }

    @Test (expectedExceptions = GuidGenerationException.class)
    public void testGenerateGUIDNullStream() throws Exception {
        InputStream stream = null;
        GUID.generateGUID(stream);
    }

}
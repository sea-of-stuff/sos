package sos.model.implementations.utils;

import IO.utils.StreamsUtils;
import constants.Hashes;
import org.testng.annotations.Test;
import sos.exceptions.GuidGenerationException;

import java.io.InputStream;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GUIDTest {


    @Test
    public void testGetHashHexAndSize() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUID guid = new GUIDsha1(inputStreamFake);

        assertEquals(Hashes.TEST_STRING_HASHED, guid.toString());
    }

    @Test (expectedExceptions = GuidGenerationException.class)
    public void testNoInputStream() throws Exception {
        GUID guid = new GUIDsha1(null);

        assertEquals(Hashes.TEST_STRING_HASHED, guid.toString());
    }
}
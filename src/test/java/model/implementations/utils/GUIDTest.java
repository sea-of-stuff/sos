package model.implementations.utils;

import IO.utils.StreamsUtils;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GUIDTest {

    private static final int SHA_1_SIZE_IN_BYTES = 20;
    private static final String TEST_STRING = "TEST";
    // Hash generated using http://www.sha1-online.com/
    private static final String TEST_STRING_HASHED = "984816fd329622876e14907634264e6f332e9fb3";

    @Test
    public void testGetAlgorithm() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        assertEquals("sha-1", guid.getAlgorithm());
    }

    @Test
    public void testGetHashHexAndSize() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(TEST_STRING);
        GUID guid = new GUIDsha1(inputStreamFake);

        assertEquals(SHA_1_SIZE_IN_BYTES, guid.getHashSize());
        assertEquals(TEST_STRING_HASHED, guid.getHashHex());
    }
}
package uk.ac.standrews.cs.sos.model.implementations.utils;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.IO.utils.StreamsUtils;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.constants.Hashes;

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

}
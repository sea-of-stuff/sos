package uk.ac.standrews.cs.sos.model.utils;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;
import uk.ac.standrews.cs.utils.GUIDFactory;
import uk.ac.standrews.cs.utils.IGUID;
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
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);
        assertEquals(Hashes.TEST_STRING_HASHED, guid.toString());
    }

    @Test (expectedExceptions = GUIDGenerationException.class)
    public void testNullStream() throws Exception {
        InputStream stream = null;
        GUIDFactory.generateGUID(stream);
    }

    @Test
    public void testGenerateGUID() throws Exception {
        IGUID guid = GUIDFactory.generateGUID(Hashes.TEST_STRING);
        assertEquals(Hashes.TEST_STRING_HASHED, guid.toString());
    }

    @Test (expectedExceptions = GUIDGenerationException.class)
    public void testGenerateGUIDNullString() throws Exception {
        String string = null;
        GUIDFactory.generateGUID(string);
    }

    @Test (expectedExceptions = GUIDGenerationException.class)
    public void testGenerateGUIDNullStream() throws Exception {
        InputStream stream = null;
        GUIDFactory.generateGUID(stream);
    }

}
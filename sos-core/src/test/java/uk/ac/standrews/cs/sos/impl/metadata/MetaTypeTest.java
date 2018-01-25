package uk.ac.standrews.cs.sos.impl.metadata;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetaTypeTest extends CommonTest {

    @Test
    public void metatypeToStringTest() {

        assertEquals(MetaType.LONG.toString(), "long");
        assertEquals(MetaType.DOUBLE.toString(), "double");
        assertEquals(MetaType.BOOLEAN.toString(), "boolean");
        assertEquals(MetaType.STRING.toString(), "string");
        assertEquals(MetaType.GUID.toString(), "guid");
        assertEquals(MetaType.ANY.toString(), "any");
    }
}

package uk.ac.standrews.cs.sos.SOSImpl.Client.standard;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddMetadataTest extends ClientTest {

    @Test
    public void testAddAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);

        InputStream inputStream = location.getSource();
        SOSMetadata metadata = client.addMetadata(inputStream);

        assertEquals(1, metadata.getAllPropertyNames().length);
        assertEquals("text/plain; charset=ISO-8859-1", metadata.getProperty("Content-Type"));
    }
}

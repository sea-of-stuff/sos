package uk.ac.standrews.cs.sos.model.locations;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;
import java.net.MalformedURLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocationTest extends SetUpTest {

    private static IGUID NODE_GUID = GUIDFactory.generateRandomGUID();
    private static IGUID DATA_GUID = GUIDFactory.generateRandomGUID();

    @Test
    public void testGetURI() throws Exception {
        SOSLocation location = new SOSLocation(NODE_GUID, DATA_GUID);
        assertEquals(location.getURI().toString(), "sos://" +
                NODE_GUID.toString() + "/" +
                DATA_GUID.toString());
    }

    @Test
    public void testMakeURIFromString() throws Exception {
        SOSLocation location = new SOSLocation(NODE_GUID, DATA_GUID);
        SOSLocation stringLocation = new SOSLocation("sos://" +
                NODE_GUID.toString() + "/" +
                DATA_GUID.toString());
        assertEquals(stringLocation, location);
    }

    @Test
    public void testGetSource() throws Exception {
        HelperTest.createDummyDataFile(internalStorage, DATA_GUID.toString());

        SOSLocation location = new SOSLocation(localSOSNode.getNodeGUID(), DATA_GUID);
        InputStream inputStream = location.getSource();
        String retrieved = HelperTest.InputStreamToString(inputStream);

        assertTrue(retrieved.contains("The first line"));
        assertTrue(retrieved.contains("The second line"));
    }

    @Test (expectedExceptions = MalformedURLException.class)
    public void wrongURINoNodeGUIDTest() throws Exception {
        new SOSLocation("sos://" +
                "/" +
                DATA_GUID.toString());
    }

    @Test (expectedExceptions = MalformedURLException.class)
    public void wrongURINoDataGUIDTest() throws Exception {
        new SOSLocation("sos://" +
                NODE_GUID.toString() + "/");
    }

    @Test (expectedExceptions = MalformedURLException.class)
    public void wrongURINoSlashTest() throws Exception {
        new SOSLocation("sos://" +
                NODE_GUID.toString() +
                DATA_GUID.toString());
    }

}

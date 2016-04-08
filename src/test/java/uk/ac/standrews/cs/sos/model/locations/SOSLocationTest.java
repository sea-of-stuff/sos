package uk.ac.standrews.cs.sos.model.locations;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.utils.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocationTest extends SetUpTest {

    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() throws Exception {
        SeaConfiguration.setRootName("test");
        configuration = SeaConfiguration.getInstance();
        configuration.setNodeId(GUIDFactory.recreateGUID("12345678"));

        try {
            URL.setURLStreamHandlerFactory(new SOSURLStreamHandlerFactory());
        } catch (Error e) {
            // Error is thrown if factory was already setup in previous tests
        }
    }

    @AfterMethod
    public void tearDown() throws IOException {
        Helper.cleanDirectory(configuration.getCacheDataPath());
    }

    @Test
    public void testGetURI() throws Exception {
        SOSLocation location = new SOSLocation(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("abc"));
        assertEquals(location.getURI().toString(), "sos://0000000000000000000000000000000000000123/0000000000000000000000000000000000000abc");
    }

    @Test
    public void testMakeURIFromString() throws Exception {
        SOSLocation location = new SOSLocation(GUIDFactory.recreateGUID("123"), GUIDFactory.recreateGUID("abc"));
        SOSLocation stringLocation = new SOSLocation("sos://0000000000000000000000000000000000000123/0000000000000000000000000000000000000abc");
        assertEquals(stringLocation, location);
    }

    @Test
    public void testGetSource() throws Exception {
        Helper.createDummyDataFile(SeaConfiguration.getInstance().getCacheDataPath(), "0000000000000000000000000000000000000abc");

        SOSLocation location = new SOSLocation(configuration.getNodeId(), GUIDFactory.recreateGUID("abc"));
        InputStream inputStream = location.getSource();
        String retrieved = IOUtils.toString(inputStream);

        assertTrue(retrieved.contains("The first line"));
        assertTrue(retrieved.contains("The second line"));
    }

}

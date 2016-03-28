package uk.ac.standrews.cs.sos.model.locations;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.utils.GUIDsha1;
import uk.ac.standrews.cs.utils.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocationTest extends SetUpTest {

    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() throws IOException, SeaConfigurationException {
        SeaConfiguration.setRootName("test");
        configuration = SeaConfiguration.getInstance();
        configuration.setNodeId(new GUIDsha1("12345678"));

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
        SOSLocation location = new SOSLocation(new GUIDsha1("123"), new GUIDsha1("abc"));
        assertEquals(location.getURI().toString(), "sos://123/abc");
    }

    @Test
    public void testMakeURIFromString() throws Exception {
        SOSLocation location = new SOSLocation(new GUIDsha1("123"), new GUIDsha1("abc"));
        SOSLocation stringLocation = new SOSLocation("sos://123/abc");
        assertEquals(stringLocation, location);
    }

    @Test
    public void testGetSource() throws Exception {
        Helper.createDummyDataFile(SeaConfiguration.getInstance().getCacheDataPath(), "abc");

        SOSLocation location = new SOSLocation(configuration.getNodeId(), new GUIDsha1("abc"));
        InputStream inputStream = location.getSource();
        String retrieved = IOUtils.toString(inputStream);

        assertTrue(retrieved.contains("The first line"));
        assertTrue(retrieved.contains("The second line"));
    }

    @Test (enabled = false)
    public void testGetAnySourceFromOtherNode() throws Exception {
        Helper.createDummyDataFile(SeaConfiguration.getInstance().getCacheDataPath(), "abc");

        SOSLocation location = new SOSLocation(new GUIDsha1("123"), new GUIDsha1("abc"));
        InputStream inputStream = location.getSource();

        assertNotNull(inputStream);
    }

}

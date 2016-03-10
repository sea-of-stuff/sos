package uk.ac.standrews.cs.sos.model.implementations.locations;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocationTest extends SetUpTest {

    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() throws IOException {
        configuration = SeaConfiguration.getInstance();

        try {
            URL.setURLStreamHandlerFactory(new SOSURLStreamHandlerFactory());
        } catch (Error e) {
            // Error is thrown if factory was already setup in previous tests
        }
    }

    @AfterMethod
    public void tearDown() throws IOException {
        FileUtils.cleanDirectory(new File(configuration.getCacheDataPath()));
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

        SOSLocation location = new SOSLocation(new GUIDsha1("abcdefg12345"), new GUIDsha1("abc"));
        InputStream inputStream = location.getSource();
        String retrieved = IOUtils.toString(inputStream);

        assertTrue(retrieved.contains("The first line"));
        assertTrue(retrieved.contains("The second line"));
    }

    @Test
    public void testGetAnySourceFromOtherNode() throws Exception {
        Helper.createDummyDataFile(SeaConfiguration.getInstance().getCacheDataPath(), "abc");

        SOSLocation location = new SOSLocation(new GUIDsha1("123"), new GUIDsha1("abc"));
        InputStream inputStream = location.getSource();

        assertNotNull(inputStream);
    }

}

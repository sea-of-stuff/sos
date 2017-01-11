package uk.ac.standrews.cs.sos.model.metadata;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadata;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.InputStreamData;
import uk.ac.standrews.cs.storage.data.StringData;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngineTest extends CommonTest {

    @Test
    public void basicMetadataTest() throws MetadataException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Data data = new StringData("just some text in a string");
        TikaMetadata output = test.processData(data);

        assertEquals(4, output.getAllFilteredPropertyNames().length);
        assertEquals("text/plain; charset=ISO-8859-1", output.getProperty("Content-Type"));
        assertEquals("org.apache.tika.parser.DefaultParser", output.getProperty("X-Parsed-By"));
        assertEquals("26", output.getProperty("Size"));
        assertNotNull(output.getProperty("Timestamp"));
    }

    @Test
    public void ignorePropertiesTest() throws MetadataException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Data data = new StringData("just some text in a string");
        TikaMetadata output = test.processData(data);

        String contentEncoding = output.getProperty("Content-Encoding");
        assertNull(contentEncoding);
    }

    @Test
    public void parseImageTest() throws MetadataException, URISyntaxException, IOException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Location location = new URILocation("http://www.planwallpaper.com/static/images/cool-background.jpg");
        Data data = new InputStreamData(location.getSource());
        TikaMetadata output = test.processData(data);

        String[] props = output.getAllPropertyNames();
        for(String prop:props) {
            System.out.println(prop);
            System.out.println(">> " + output.getProperty(prop));
        }

    }

    // TODO - download data first
    @Test (enabled = false)
    public void parseLocalImageTest() throws MetadataException, URISyntaxException, IOException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Location location = new URILocation("file:///Users/sic2/Downloads/isamuni-logo3.png");
        Data data = new InputStreamData(location.getSource());
        TikaMetadata output = test.processData(data);

        String[] props = output.getAllPropertyNames();
        for(String prop:props) {
            System.out.println(prop);
            System.out.println(">> " + output.getProperty(prop));
        }

    }

    // TODO - download data first
    @Test (enabled = false)
    public void parseLocalImagenoextTest() throws MetadataException, URISyntaxException, IOException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Location location = new URILocation("file:///Users/sic2/sos/data/90e64f5c9baff995b4f7a8eaee3d1dd68c6538fe");
        Data data = new InputStreamData(location.getSource());
        TikaMetadata output = test.processData(data);

        String[] props = output.getAllPropertyNames();
        for(String prop:props) {
            System.out.println(prop);
            System.out.println(">> " + output.getProperty(prop));
        }

    }


}

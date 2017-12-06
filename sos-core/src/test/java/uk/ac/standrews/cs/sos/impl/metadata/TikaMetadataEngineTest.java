package uk.ac.standrews.cs.sos.impl.metadata;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngineTest extends CommonTest {

    @Test
    public void basicMetadataTest() throws MetadataException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Data data = new StringData("just some text in a string");
        MetadataBuilder metadataBuilder = new MetadataBuilder().setData(data);
        Metadata output = test.processData(metadataBuilder);

        assertEquals(5, output.getAllPropertyNames().length);
        assertEquals("text/plain; charset=ISO-8859-1", output.getProperty("Content-Type").getValue_s());
        assertEquals("ISO-8859-1", output.getProperty("Content-Encoding").getValue_s());
        assertEquals("org.apache.tika.parser.DefaultParser", output.getProperty("X-Parsed-By").getValue_s());
        assertEquals(26L, output.getProperty("Size").getValue_l());
        assertNotNull(output.getProperty("Timestamp"));
    }

    @Test
    public void parseImageTest() throws MetadataException, URISyntaxException, IOException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Location location = new URILocation("http://www.planwallpaper.com/static/images/cool-background.jpg");
        Data data = new InputStreamData(location.getSource());
        MetadataBuilder metadataBuilder = new MetadataBuilder().setData(data);
        Metadata output = test.processData(metadataBuilder);

        String[] props = output.getAllPropertyNames();
        for(String prop:props) {
            System.out.println(prop);
            System.out.println(">> " + output.getProperty(prop));
        }

    }

}

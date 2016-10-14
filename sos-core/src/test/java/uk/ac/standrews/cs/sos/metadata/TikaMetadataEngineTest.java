package uk.ac.standrews.cs.sos.metadata;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngineTest {

    @Test
    public void basicMetadata() throws SOSMetadataException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Data data = new StringData("just some text in a string");
        TikaMetadata output = test.processData(data);

        assertEquals(3, output.getAllPropertyNames().length);
        assertEquals("org.apache.tika.parser.DefaultParser", output.getProperty("X-Parsed-By"));
        assertEquals("ISO-8859-1", output.getProperty("Content-Encoding"));
        assertEquals("text/plain; charset=ISO-8859-1", output.getProperty("Content-Type"));
    }

}

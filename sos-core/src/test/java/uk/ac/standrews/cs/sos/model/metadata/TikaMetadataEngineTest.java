package uk.ac.standrews.cs.sos.model.metadata;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadata;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngineTest extends CommonTest {

    @Test
    public void basicMetadataTest() throws SOSMetadataException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Data data = new StringData("just some text in a string");
        TikaMetadata output = test.processData(data);

        assertEquals(2, output.getAllFilteredPropertyNames().length);
        assertEquals("text/plain; charset=ISO-8859-1", output.getProperty("Content-Type"));
        assertEquals("org.apache.tika.parser.DefaultParser", output.getProperty("X-Parsed-By"));
    }

    @Test
    public void ignorePropertiesTest() throws SOSMetadataException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Data data = new StringData("just some text in a string");
        TikaMetadata output = test.processData(data);

        String contentEncoding = output.getProperty("Content-Encoding");
        assertNull(contentEncoding);
    }

}

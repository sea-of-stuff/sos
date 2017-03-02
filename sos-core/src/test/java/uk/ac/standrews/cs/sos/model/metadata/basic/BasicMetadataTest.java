package uk.ac.standrews.cs.sos.model.metadata.basic;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * Testing the metadata serialiser/desearialiser
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicMetadataTest {

    @Test
    public void metadataDeserializationTest() throws IOException {

        String testMetadata =
                " {\n" +
                        " \t\"GUID\": \"3f845edc76b7e892ddca1f6e290750fe805e7f00\",\n" +
                        " \t\"Properties\": {\n" +
                        " \t\t\"X-Parsed-By\": \"org.apache.tika.parser.DefaultParser\",\n" +
                        " \t\t\"Content-Encoding\": \"null\",\n" +
                        " \t\t\"Size\": \"26\",\n" +
                        " \t\t\"Timestamp\": \"1484736105\",\n" +
                        " \t\t\"Content-Type\": \"text/plain; charset=ISO-8859-1\"\n" +
                        " \t}\n" +
                        " }";

        BasicMetadata metadata = JSONHelper.JsonObjMapper().readValue(testMetadata, BasicMetadata.class);

        assertEquals(metadata.getProperty("X-Parsed-By"), "org.apache.tika.parser.DefaultParser");
        assertEquals(metadata.getProperty("Size"), "26");
        assertEquals(metadata.getProperty("Content-Encoding"), "null");
        assertEquals(metadata.getProperty("Timestamp"), "1484736105");
        assertEquals(metadata.getProperty("Content-Type"), "text/plain; charset=ISO-8859-1");
    }
}

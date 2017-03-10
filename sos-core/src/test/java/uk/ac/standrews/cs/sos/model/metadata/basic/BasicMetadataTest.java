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
                        "   \"GUID\": \"02f80108b23125787b8bccc2b80ec623e2dffcd6\",\n" +
                        "   \"Properties\": [\n" +
                        "     { \"X-Parsed-By\": \"org.apache.tika.parser.DefaultParser\" },\n" +
                        "     { \"Content-Encoding\": \"null\" },\n" +
                        "     { \"Size\": 26 },\n" +
                        "     { \"Timestamp\": 1484736105 },\n" +
                        "     { \"Content-Type\": \"text/plain; charset=ISO-8859-1\" }\n" +
                        "   ]\n" +
                        " }";

        BasicMetadata metadata = JSONHelper.JsonObjMapper().readValue(testMetadata, BasicMetadata.class);

        assertEquals(metadata.getProperty("X-Parsed-By"), "org.apache.tika.parser.DefaultParser");
        assertEquals(metadata.getProperty("Size"), 26);
        assertEquals(metadata.getProperty("Content-Encoding"), "null");
        assertEquals(metadata.getProperty("Timestamp"), 1484736105);
        assertEquals(metadata.getProperty("Content-Type"), "text/plain; charset=ISO-8859-1");
    }
}

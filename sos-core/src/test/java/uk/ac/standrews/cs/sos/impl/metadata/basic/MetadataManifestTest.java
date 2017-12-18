package uk.ac.standrews.cs.sos.impl.metadata.basic;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * Testing the metadata serialiser/desearialiser
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataManifestTest {

    @Test
    public void metadataDeserializationTest() throws IOException {

        String testMetadata =
                "{\n" +
                        "    \"GUID\": \"SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0\",\n" +
                        "    \"type\":\"Metadata\"," +
                        "    \"properties\": [\n" +
                        "        {\n" +
                        "            \"key\": \"X-Parsed-By\",\n" +
                        "            \"type\": \"String\",\n" +
                        "            \"value\": \"org.apache.tika.parser.DefaultParser\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"key\": \"Content-Encoding\",\n" +
                        "            \"type\": \"String\",\n" +
                        "            \"value\": \"null\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"key\": \"Size\",\n" +
                        "            \"type\": \"Long\",\n" +
                        "            \"value\": 26\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"key\": \"Timestamp\",\n" +
                        "            \"type\": \"Long\",\n" +
                        "            \"value\": 1484736105\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"key\": \"Content-Type\",\n" +
                        "            \"type\": \"String\",\n" +
                        "            \"value\": \"text/plain; charset=ISO-8859-1\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";


        Metadata metadata = JSONHelper.jsonObjMapper().readValue(testMetadata, Metadata.class);

        assertEquals(metadata.getProperty("X-Parsed-By").getValue_s(), "org.apache.tika.parser.DefaultParser");
        assertEquals(metadata.getProperty("Size").getValue_l(), 26L);
        assertEquals(metadata.getProperty("Content-Encoding").getValue_s(), "null");
        assertEquals(metadata.getProperty("Timestamp").getValue_l(), 1484736105);
        assertEquals(metadata.getProperty("Content-Type").getValue_s(), "text/plain; charset=ISO-8859-1");
    }
}

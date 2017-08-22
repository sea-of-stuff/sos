package uk.ac.standrews.cs.sos.impl.metadata.basic;

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
                "{\n" +
                        "    \"GUID\": \"SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0\",\n" +
                        "    \"Properties\": [\n" +
                        "        {\n" +
                        "            \"Key\": \"X-Parsed-By\",\n" +
                        "            \"Type\": \"String\",\n" +
                        "            \"Value\": \"org.apache.tika.parser.DefaultParser\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"Key\": \"Content-Encoding\",\n" +
                        "            \"Type\": \"String\",\n" +
                        "            \"Value\": \"null\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"Key\": \"Size\",\n" +
                        "            \"Type\": \"Long\",\n" +
                        "            \"Value\": 26\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"Key\": \"Timestamp\",\n" +
                        "            \"Type\": \"int\",\n" +
                        "            \"Value\": 1484736105\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"Key\": \"Content-Type\",\n" +
                        "            \"Type\": \"String\",\n" +
                        "            \"Value\": \"text/plain; charset=ISO-8859-1\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";


        BasicMetadata metadata = JSONHelper.JsonObjMapper().readValue(testMetadata, BasicMetadata.class);

        assertEquals(metadata.getProperty("X-Parsed-By"), "org.apache.tika.parser.DefaultParser");
        assertEquals(metadata.getProperty("Size"), 26L);
        assertEquals(metadata.getProperty("Content-Encoding"), "null");
        assertEquals(metadata.getProperty("Timestamp"), 1484736105);
        assertEquals(metadata.getProperty("Content-Type"), "text/plain; charset=ISO-8859-1");
    }
}

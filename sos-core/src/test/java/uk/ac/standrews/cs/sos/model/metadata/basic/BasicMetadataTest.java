package uk.ac.standrews.cs.sos.model.metadata.basic;

import org.junit.Test;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicMetadataTest {

    // TODO - test json deserialization

    @Test
    public void metadataDeserializationTest() throws IOException {

        String testMetadata =
                "{\n" +
                        "  \"GUID\" : \"a7fe358986a3757dc9e5222ba8024fa80f43e4dd\",\n" +
                        "  \"Properties\" : [ {\n" +
                        "    \"Key\" : \"X-Parsed-By\",\n" +
                        "    \"Value\" : \"org.apache.tika.parser.DefaultParser\"\n" +
                        "  }, {\n" +
                        "    \"Key\" : \"Size\",\n" +
                        "    \"Value\" : \"26\"\n" +
                        "  }, {\n" +
                        "    \"Key\" : \"Content-Encoding\",\n" +
                        "    \"Value\" : null\n" +
                        "  }, {\n" +
                        "    \"Key\" : \"Timestamp\",\n" +
                        "    \"Value\" : \"1484736105\"\n" +
                        "  }, {\n" +
                        "    \"Key\" : \"Content-Type\",\n" +
                        "    \"Value\" : \"text/plain; charset=ISO-8859-1\"\n" +
                        "  } ]\n" +
                        "}";

        BasicMetadata metadata = JSONHelper.JsonObjMapper().readValue(testMetadata, BasicMetadata.class);
        System.out.println(metadata.toString());
    }
}

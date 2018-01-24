package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.impl.context.examples.ReferencePolicy;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicySerializerTest {

    @Test
    public void serializeReferencePredicate() throws IOException {

        String JSON_POLICY =
            "{\n" +
            "  \"type\": \"Policy\",\n" +
                    "  \"apply\": \"\",\n" +
                    "  \"satisfied\": \"return true;\",\n" +
                    "  \"fields\": [{\n" +
                    "    \"type\": \"int\",\n" +
                    "    \"name\": \"factor\",\n" +
                    "    \"value\": \"2\"\n" +
                    "  }]\n" +
                    "}";
        JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(JSON_POLICY);

        String expected = "{\n" +
                "  \"type\" : \"Policy\",\n" +
                "  \"guid\" : \"SHA256_16_8910463e9de02413720c413e6dcf569cc4de73b32f03bc88571f455199558844\",\n" +
                "  \"apply\" : \"\",\n" +
                "  \"satisfied\" : \"return true;\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"type\" : \"int\",\n" +
                "    \"name\" : \"factor\",\n" +
                "    \"value\" : \"2\"\n" +
                "  } ]\n" +
                "}";

        ReferencePolicy referencePolicy = new ReferencePolicy(jsonNode);
        System.out.println(referencePolicy.toString());
        assertEquals(referencePolicy.toString(), expected);
    }
}
package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.impl.context.examples.ReferencePredicate;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateSerializerTest {

    @Test
    public void serializeReferencePredicate() throws IOException {

        String JSON_PREDICATE =
                "{\n" +
                        "\t\"type\": \"Predicate\",\n" +
                        "\t\"predicate\": \"true;\",\n" +
                        "\t\"dependencies\": [ \"EXAMPLE\", \"HELLO\" ]\n" +
                        "}";
        JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(JSON_PREDICATE);

        String expected = "{\n" +
                "  \"type\" : \"Predicate\",\n" +
                "  \"GUID\" : \"SHA256_16_90cf94ec60bab7127adf5c9646ae9e23fe32276f66f2393d7c6f21744713e369\",\n" +
                "  \"predicate\" : \"true;\",\n" +
                "  \"dependencies\" : [ \"EXAMPLE\", \"HELLO\" ]\n" +
                "}";

        ReferencePredicate referencePredicate = new ReferencePredicate(jsonNode);
        assertEquals(referencePredicate.toString(), expected);
    }
}

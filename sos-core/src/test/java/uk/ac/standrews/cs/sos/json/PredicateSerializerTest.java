package uk.ac.standrews.cs.sos.json;

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
                        "\t\"Type\": \"Predicate\",\n" +
                        "\t\"Predicate\": \"true;\",\n" +
                        "\t\"Dependencies\": [ \"EXAMPLE\", \"HELLO\" ]\n" +
                        "}";
        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(JSON_PREDICATE);

        String expected = "{\n" +
                "  \"Type\" : \"Predicate\",\n" +
                "  \"GUID\" : \"SHA256_16_054ae7aafe6d9e1fedab97f9952f7da4698604eabe3426397b8742362bd7f464\",\n" +
                "  \"Dependencies\" : [ \"EXAMPLE\", \"HELLO\" ],\n" +
                "  \"Predicate\" : \"true;\"\n" +
                "}";

        ReferencePredicate referencePredicate = new ReferencePredicate(jsonNode, 1);
        assertEquals(referencePredicate.toString(), expected);
    }
}

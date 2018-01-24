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
                        "\t\"predicate\": \"true;\"\n" +
                        "}";
        JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(JSON_PREDICATE);

        String expected = "{\n" +
                "  \"type\" : \"Predicate\",\n" +
                "  \"guid\" : \"SHA256_16_bb568e69b0b8b04afc620a4c7727b2f7d9e029bd02de8c1284268e9a03b68d6e\",\n" +
                "  \"predicate\" : \"true;\"\n" +
                "}";

        ReferencePredicate referencePredicate = new ReferencePredicate(jsonNode);
        assertEquals(referencePredicate.toString(), expected);
    }
}

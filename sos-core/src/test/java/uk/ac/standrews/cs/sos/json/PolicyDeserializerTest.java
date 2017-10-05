package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.Iterator;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyDeserializerTest extends SetUpTest {

    @Test
    public void deserializeSimplePolicy() throws IOException, PolicyException {

        String policyJSON = "{\n" +
                "  \"type\" : \"Policy\",\n" +
                "  \"GUID\" : \"SHA256_16_bfb31cfd5fbfd1bdf7e85cd4f12d557bcd21afb9f8bfd95b877bb4674a4d6c8d\",\n" +
                "  \"dependencies\" : [ ],\n" +
                "  \"apply\" : \"\",\n" +
                "  \"satisfied\" : \"return true;\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"type\" : \"int\",\n" +
                "    \"name\" : \"factor\",\n" +
                "    \"value\" : \"2\"\n" +
                "  } ]\n" +
                "}";

        Policy policy = JSONHelper.JsonObjMapper().readValue(policyJSON, Policy.class);
        assertNotNull(policy);
        assertNotNull(policy.guid());
        assertNotNull(policy.fields());
        assertNotNull(policy.apply());
        assertNotNull(policy.satisfied());
        assertNotNull(policy.dependencies());

        Iterator<JsonNode> fields_n = policy.fields().iterator();
        assertTrue(fields_n.hasNext());
        JsonNode field = fields_n.next();
        assertEquals(field.get("type").asText(), "int");
        assertEquals(field.get("name").asText(), "factor");
        assertEquals(field.get("value").asText(), "2");

        assertEquals(policy.apply().asText(), "");
        assertEquals(policy.satisfied().asText(), "return true;");

        assertTrue(policy.satisfied(null, null, null));
    }
}
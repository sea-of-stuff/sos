package uk.ac.standrews.cs.sos.json;

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
            "  \"Type\": \"Policy\",\n" +
                    "  \"Apply\": \"\",\n" +
                    "  \"Satisfied\": \"return true;\",\n" +
                    "  \"Dependencies\": [],\n" +
                    "  \"Fields\": [{\n" +
                    "    \"Type\": \"int\",\n" +
                    "    \"Name\": \"factor\",\n" +
                    "    \"Value\": \"2\"\n" +
                    "  }]\n" +
                    "}";
        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(JSON_POLICY);

        String expected = "{\n" +
                "  \"Type\" : \"Policy\",\n" +
                "  \"GUID\" : \"SHA256_16_bfb31cfd5fbfd1bdf7e85cd4f12d557bcd21afb9f8bfd95b877bb4674a4d6c8d\",\n" +
                "  \"Dependencies\" : [ ],\n" +
                "  \"Apply\" : \"\",\n" +
                "  \"Satisfied\" : \"return true;\",\n" +
                "  \"Fields\" : [ {\n" +
                "    \"Type\" : \"int\",\n" +
                "    \"Name\" : \"factor\",\n" +
                "    \"Value\" : \"2\"\n" +
                "  } ]\n" +
                "}";

        ReferencePolicy referencePolicy = new ReferencePolicy(jsonNode);
        System.out.println(referencePolicy.toString());
        assertEquals(referencePolicy.toString(), expected);
    }
}
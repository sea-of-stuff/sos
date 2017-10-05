package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.model.Predicate;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.Iterator;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateDeserializerTest extends SetUpTest {

    @Test
    public void deserializeSimplePredicate() throws IOException {

        String predicateJSON = "{\n" +
                "  \"type\" : \"Predicate\",\n" +
                "  \"GUID\" : \"SHA256_16_054ae7aafe6d9e1fedab97f9952f7da4698604eabe3426397b8742362bd7f464\",\n" +
                "  \"dependencies\" : [ \"java.util.Set\", \"java.util.Iterator\" ],\n" +
                "  \"predicate\" : \"true;\"\n" +
                "}";

        Predicate predicate = JSONHelper.JsonObjMapper().readValue(predicateJSON, Predicate.class);
        assertNotNull(predicate);
        assertNotNull(predicate.guid());
        assertNotNull(predicate.predicate());
        assertNotNull(predicate.dependencies());

        assertEquals(predicate.predicate().asText(), "true;");

        Iterator<JsonNode> dependencies_n = predicate.dependencies().iterator();
        assertTrue(dependencies_n.hasNext());
        JsonNode dependency = dependencies_n.next();
        assertEquals(dependency.asText(), "java.util.Set");

        assertTrue(dependencies_n.hasNext());
        dependency = dependencies_n.next();
        assertEquals(dependency.asText(), "java.util.Iterator");
    }
}
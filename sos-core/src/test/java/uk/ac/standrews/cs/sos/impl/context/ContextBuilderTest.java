package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.context.ContextBuilderException;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextBuilderTest extends SetUpTest {

    @Test
    public void fatStringToContext() throws IOException, ContextBuilderException {

        String FATContext = "{\n" +
                "\t\"context\": {\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"domain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"codomain\": {\n" +
                "\t\t\t\"type\": \"SPECIFIED\",\n" +
                "\t\t\t\"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t}]\n" +
                "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(FATContext);
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        assertNotNull(contextBuilder.predicate());
        Predicate predicate = JSONHelper.JsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);
        assertNotNull(predicate);

        assertNotNull(contextBuilder.policies());
        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.JsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }
        assertEquals(policies.size(), 1);

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());
        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.JsonObjMapper().convertValue(context_n, Context.class);
        assertNotNull(context);
        assertNotNull(context.guid());
        assertNotNull(context.previous());
        assertTrue(context.previous().isEmpty());
        assertEquals(context.maxAge(), 0);

        NodesCollection domain = context.domain();
        assertEquals(domain.type(), NodesCollectionType.LOCAL);
        assertTrue(domain.nodesRefs().isEmpty());

        NodesCollection codomain = context.codomain();
        assertEquals(codomain.type(), NodesCollectionType.SPECIFIED);
        assertFalse(codomain.nodesRefs().isEmpty());
        assertEquals(codomain.nodesRefs().size(), 1);
    }


    @Test
    public void contextToFATString() throws IOException, ContextBuilderException {

        String FATContext = "{\n" +
                "\t\"context\": {\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"domain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"codomain\": {\n" +
                "\t\t\t\"type\": \"SPECIFIED\",\n" +
                "\t\t\t\"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t}]\n" +
                "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(FATContext);
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        Predicate predicate = JSONHelper.JsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);

        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.JsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());
        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.JsonObjMapper().convertValue(context_n, Context.class);

        // Testing the toFATString method
        String reFATContext = context.toFATString(predicate, policies);
        JSONAssert.assertEquals(FATContext, reFATContext, false);
    }
}
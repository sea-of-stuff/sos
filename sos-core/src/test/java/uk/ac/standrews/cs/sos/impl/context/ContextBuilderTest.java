/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.context.ContextBuilderException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
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
                "\t\t},\n" +
                "\t\t\"max_age\": 0\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(FATContext);
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        assertNotNull(contextBuilder.predicate());
        Predicate predicate = JSONHelper.jsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);
        assertNotNull(predicate);

        assertNotNull(contextBuilder.policies());
        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.jsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }
        assertEquals(policies.size(), 1);

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());
        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.jsonObjMapper().convertValue(context_n, Context.class);
        assertNotNull(context);
        assertNotNull(context.guid());
        assertNotNull(context.timestamp());
        assertEquals(context.getName(), "All");
        assertEquals(context.getUniqueName(), "All-" + context.guid().toMultiHash());
        assertNotNull(context.previous());
        assertTrue(context.previous().isEmpty());
        assertEquals(context.maxAge(), 0);

        NodesCollection domain = context.domain(false);
        assertEquals(domain.type(), NodesCollectionType.LOCAL);
        assertTrue(domain.nodesRefs().isEmpty());

        NodesCollection codomain = context.codomain();
        assertEquals(codomain.type(), NodesCollectionType.SPECIFIED);
        assertFalse(codomain.nodesRefs().isEmpty());
        assertEquals(codomain.nodesRefs().size(), 1);
    }

    @Test
    public void fatToContextV2() throws IOException, ContextBuilderException {

        String FATContext = "{\n" +
                "  \"context\" : {\n" +
                "    \"name\" : \"predicate_2\",\n" +
                "    \"domain\" : {\n" +
                "      \"type\" : \"SPECIFIED\",\n" +
                "      \"nodes\" : [ \"SHA256_16_aed7bbf1e6ef5c8d22162c096ab069b8d2056696be262551951660aac6d836ef\" %_LOCAL_NODE_% ]\n" +
                "    },\n" +
                "    \"codomain\" : {\n" +
                "      \"type\" : \"LOCAL\",\n" +
                "      \"nodes\" : [ ]\n" +
                "    },\n" +
                "    \"max_age\" : 0\n" +
                "  },\n" +
                "  \"predicate\" : {\n" +
                "    \"type\" : \"Predicate\",\n" +
                "    \"predicate\" : \"CommonPredicates.TextOccurrencesIgnoreCase(guid, \\\"the\\\") == 1;\"\n" +
                "  },\n" +
                "  \"policies\" : [ ]\n" +
                "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(FATContext.replace("%_LOCAL_NODE_%", "")); // local node is implicit
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        assertNotNull(contextBuilder.predicate());
        Predicate predicate = JSONHelper.jsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);
        assertNotNull(predicate);

        assertNotNull(contextBuilder.policies());
        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.jsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }
        assertEquals(policies.size(), 0);

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());

        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.jsonObjMapper().convertValue(context_n, Context.class);
        assertNotNull(context);
        assertNotNull(context.guid());

        String reFATContext = context.toFATString(predicate, policies);
        IGUID localNodeGUID = SOSLocalNode.settings.guid();
        String contextToExpect = FATContext.replace("%_LOCAL_NODE_%", ", \"" + localNodeGUID.toMultiHash() + "\"");
        JSONAssert.assertEquals(contextToExpect, reFATContext, false);
    }

    @Test
    public void fatToContextV3() throws IOException, ContextBuilderException {

        String FATContext = "{\n" +
                "  \"context\" : {\n" +
                "    \"name\" : \"predicate_2\",\n" +
                "    \"domain\" : {\n" +
                "      \"type\" : \"SPECIFIED\",\n" +
                "      \"nodes\" : [ \"SHA256_16_aed7bbf1e6ef5c8d22162c096ab069b8d2056696be262551951660aac6d836ef\" %_LOCAL_NODE_% ]\n" +
                "    },\n" +
                "    \"codomain\" : {\n" +
                "      \"type\" : \"LOCAL\",\n" +
                "      \"nodes\" : [ ]\n" +
                "    },\n" +
                "    \"max_age\" : 0\n" +
                "  },\n" +
                "  \"predicate\" : {\n" +
                "    \"type\" : \"Predicate\",\n" +
                "    \"predicate\" : \"CommonPredicates.TextOccurrencesIgnoreCase(guid, \\\"the\\\") == 1;\"\n" +
                "  },\n" +
                "  \"policies\" : [ ]\n" +
                "}";

        IGUID localNodeGUID = SOSLocalNode.settings.guid();
        String contextToExpect = FATContext.replace("%_LOCAL_NODE_%", ", \"" + localNodeGUID.toMultiHash() + "\""); // local node is explicit
        JsonNode node = JSONHelper.jsonObjMapper().readTree(contextToExpect);
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        assertNotNull(contextBuilder.predicate());
        Predicate predicate = JSONHelper.jsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);
        assertNotNull(predicate);

        assertNotNull(contextBuilder.policies());
        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.jsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }
        assertEquals(policies.size(), 0);

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());

        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.jsonObjMapper().convertValue(context_n, Context.class);
        assertNotNull(context);
        assertNotNull(context.guid());

        String reFATContext = context.toFATString(predicate, policies);
        JSONAssert.assertEquals(contextToExpect, reFATContext, false);
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
                "\t\t},\n" +
                "\t\t\"max_age\": 0\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(FATContext);
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        Predicate predicate = JSONHelper.jsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);

        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.jsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());
        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.jsonObjMapper().convertValue(context_n, Context.class);

        // Testing the toFATString method
        String reFATContext = context.toFATString(predicate, policies);
        JSONAssert.assertEquals(FATContext, reFATContext, false);
    }


    @Test
    public void fatStringToContextMaxAge() throws IOException, ContextBuilderException {

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
                "\t\t},\n" +
                "\t\t\"max_age\": 123456789\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(FATContext);
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        assertNotNull(contextBuilder.predicate());
        Predicate predicate = JSONHelper.jsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);
        assertNotNull(predicate);

        assertNotNull(contextBuilder.policies());
        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.jsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }
        assertEquals(policies.size(), 1);

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());
        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.jsonObjMapper().convertValue(context_n, Context.class);
        assertNotNull(context);
        assertNotNull(context.guid());
        assertEquals(context.maxAge(), 123456789);
    }

    @Test
    public void fatToContextWithPolicy() throws IOException, ContextBuilderException {

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
                "\t\t},\n" +
                "\t\t\"max_age\": 0\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(FATContext);
        ContextBuilder contextBuilder = new ContextBuilder(node, ContextBuilder.ContextBuilderType.FAT);

        assertNotNull(contextBuilder.predicate());
        Predicate predicate = JSONHelper.jsonObjMapper().convertValue(contextBuilder.predicate(), Predicate.class);
        assertNotNull(predicate);

        assertNotNull(contextBuilder.policies());
        Set<Policy> policies = new LinkedHashSet<>();
        JsonNode policies_n = contextBuilder.policies();
        for (JsonNode policy_n : policies_n) {
            Policy policy = JSONHelper.jsonObjMapper().convertValue(policy_n, Policy.class);
            policies.add(policy);
        }
        assertEquals(policies.size(), 1);

        Set<IGUID> policiesRefs = policies.stream()
                .map(Manifest::guid)
                .collect(Collectors.toSet());
        JsonNode context_n = contextBuilder.context(predicate.guid(), policiesRefs);
        Context context = JSONHelper.jsonObjMapper().convertValue(context_n, Context.class);
        assertNotNull(context);

        String reFATContext = context.toFATString(predicate, policies);
        JSONAssert.assertEquals(reFATContext, FATContext, false);
    }

    @Test
    public void addFATContext() throws ContextException {

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
                "\t\t},\n" +
                "\t\t\"max_age\": 0\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        IGUID contextGUID = localSOSNode.getCMS().addContext(FATContext);
        assertNotNull(contextGUID);
        assertFalse(contextGUID.isInvalid());
        assertEquals(contextGUID.toMultiHash(), "SHA256_16_ff12eb06010992c83a41ffc0dc3be958cb68e481bdf58a3bcead675ad88a8c14");
    }
}
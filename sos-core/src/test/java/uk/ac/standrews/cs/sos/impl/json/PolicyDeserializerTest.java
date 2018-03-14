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
package uk.ac.standrews.cs.sos.impl.json;

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
                "  \"guid\" : \"SHA256_16_bfb31cfd5fbfd1bdf7e85cd4f12d557bcd21afb9f8bfd95b877bb4674a4d6c8d\",\n" +
                "  \"apply\" : \"\",\n" +
                "  \"satisfied\" : \"return true;\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"type\" : \"int\",\n" +
                "    \"name\" : \"factor\",\n" +
                "    \"value\" : \"2\"\n" +
                "  } ]\n" +
                "}";

        Policy policy = JSONHelper.jsonObjMapper().readValue(policyJSON, Policy.class);
        assertNotNull(policy);
        assertNotNull(policy.guid());
        assertNotNull(policy.fields());
        assertNotNull(policy.apply());
        assertNotNull(policy.satisfied());

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
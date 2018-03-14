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
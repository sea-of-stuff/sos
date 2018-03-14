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

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.model.Predicate;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateDeserializerTest extends SetUpTest {

    @Test
    public void deserializeSimplePredicate() throws IOException {

        String predicateJSON = "{\n" +
                "  \"type\" : \"Predicate\",\n" +
                "  \"guid\" : \"SHA256_16_054ae7aafe6d9e1fedab97f9952f7da4698604eabe3426397b8742362bd7f464\",\n" +
                "  \"predicate\" : \"true;\"\n" +
                "}";

        Predicate predicate = JSONHelper.jsonObjMapper().readValue(predicateJSON, Predicate.class);
        assertNotNull(predicate);
        assertNotNull(predicate.guid());
        assertNotNull(predicate.predicate());

        assertEquals(predicate.predicate().asText(), "true;");
    }
}
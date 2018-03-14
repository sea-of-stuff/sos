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
package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.PredicateDeserializer;
import uk.ac.standrews.cs.sos.impl.json.PredicateSerializer;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = PredicateSerializer.class)
@JsonDeserialize(using = PredicateDeserializer.class)
public interface Predicate extends ComputationalUnit {

    /**
     * Test the entity matching this GUID with the predicate
     *
     * @param guid of the entity to test
     * @return true if the test has passed
     */
    boolean test(IGUID guid);

    /**
     * AND this predicate with another one
     * @param other the predicate to AND
     * @return the resulting predicate
     */
    Predicate and(Predicate other);

    /**
     * OR this predicate with another one
     * @param other the predicate to OR
     * @return the resulting predicate
     */
    Predicate or(Predicate other);

    JsonNode predicate();
}

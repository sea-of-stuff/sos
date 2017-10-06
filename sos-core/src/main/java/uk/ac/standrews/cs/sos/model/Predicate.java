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

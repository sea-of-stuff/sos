package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSPredicate {

    long maxAge();

    /**
     * Test the entity matching this GUID with the predicate
     *
     * @param guid
     * @return
     */
    boolean test(IGUID guid);

    /**
     * AND this predicate with another one
     * @param other
     * @return
     */
    SOSPredicate and(SOSPredicate other);

    /**
     * OR this predicate with another one
     * @param other
     * @return
     */
    SOSPredicate or(SOSPredicate other);

    /**
     * Get the actual predicate. This is needed to implement the AND/OR methods
     * @return
     */
    Predicate predicate();

}

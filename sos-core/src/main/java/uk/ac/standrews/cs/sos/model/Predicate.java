package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.guid.IGUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Predicate {

    IGUID guid();

    /**
     * Get the max age for the validity of this predicate
     * The max age is compared against the system time, in nano seconds - System.nanoTime();
     *
     * @return the max age
     */
    long maxAge();

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
}
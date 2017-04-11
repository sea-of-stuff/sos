package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSPredicate {

    /**
     * Defines when the predicate of the context should be run
     * @return
     */
    PredicateComputationType predicateComputationType();

    boolean test(IGUID guid);
    SOSPredicate and(SOSPredicate other);
    SOSPredicate or(SOSPredicate other);

    Predicate predicate();

}

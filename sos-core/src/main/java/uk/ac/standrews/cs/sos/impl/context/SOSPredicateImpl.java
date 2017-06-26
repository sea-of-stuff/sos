package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.sos.model.SOSPredicate;

import java.util.Objects;

/**
 * This class acts mainly as a wrapper for the Java Predicate object.
 * The wrapper allows us to cleanly handle the predicate under the test function and to apply it for the and/or operators of the context.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SOSPredicateImpl implements SOSPredicate {

    private long maxAge;

    public SOSPredicateImpl(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public long maxAge() {
        return maxAge;
    }

    @Override
    public SOSPredicate and(SOSPredicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return null; // new SOSPredicateImpl(predicate.and(other.predicate()), newMaxAge);
    }

    @Override
    public SOSPredicate or(SOSPredicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return null; // new SOSPredicateImpl(predicate.or(other.predicate()), newMaxAge);
    }

}

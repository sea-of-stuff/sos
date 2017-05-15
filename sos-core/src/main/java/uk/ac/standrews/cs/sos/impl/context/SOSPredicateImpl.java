package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.SOSPredicate;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * This class acts mainly as a wrapper for the Java Predicate object.
 * The wrapper allows us to cleanly handle the predicate under the test function and to apply it for the and/or operators
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSPredicateImpl implements SOSPredicate {

    private Predicate<IGUID> predicate;
    private long maxAge;

    public SOSPredicateImpl(Predicate<IGUID> predicate, long maxAge) {
        this.predicate = predicate;
        this.maxAge = maxAge;
    }

    @Override
    public long maxAge() {
        return maxAge;
    }

    @Override
    public boolean test(IGUID guid) {
        return predicate.test(guid);
    }

    @Override
    public SOSPredicate and(SOSPredicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return new SOSPredicateImpl(predicate.and(other.predicate()), newMaxAge);
    }

    @Override
    public SOSPredicate or(SOSPredicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return new SOSPredicateImpl(predicate.or(other.predicate()), newMaxAge);
    }

    @Override
    public Predicate predicate() {
        return predicate;
    }

}

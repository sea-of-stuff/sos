package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.sos.impl.manifests.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Predicate;

import java.util.Objects;

/**
 * This class acts mainly as a wrapper for the Java Predicate object.
 * The wrapper allows us to cleanly handle the predicate under the test function and to apply it for the and/or operators of the context.
 *
 * TODO - and/or operations are not fully implemented yet
 * TODO - how is the predicate converted to a first class entity?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class PredicateImpl extends BasicManifest implements Predicate {

    private long maxAge;

    public PredicateImpl(long maxAge) {
        super(ManifestType.PREDICATE);

        this.maxAge = maxAge;
    }

    @Override
    public long maxAge() {
        return maxAge;
    }

    @Override
    public Predicate and(Predicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return null; // new SOSPredicateImpl(predicate.and(other.predicate()), newMaxAge);
    }

    @Override
    public Predicate or(Predicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return null; // new SOSPredicateImpl(predicate.or(other.predicate()), newMaxAge);
    }

}

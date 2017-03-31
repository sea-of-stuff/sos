package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.model.PredicateComputationType;
import uk.ac.standrews.cs.sos.interfaces.model.SOSPredicate;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSPredicateImpl implements SOSPredicate {

    private Predicate<IGUID> predicate;

    public SOSPredicateImpl(Predicate<IGUID> predicate) {
        this.predicate = predicate;
    }

    @Override
    public PredicateComputationType predicateComputationType() {
        return PredicateComputationType.AFTER_STORING;
    }

    @Override
    public boolean test(IGUID guid) {
        return predicate.test(guid);
    }

    @Override
    public int predicateFrequency() {
        return 0;
    }

    @Override
    public SOSPredicate and(SOSPredicate other) {
        Objects.requireNonNull(other);
        return new SOSPredicateImpl(predicate.and(other.predicate()));
    }

    @Override
    public SOSPredicate or(SOSPredicate other) {
        Objects.requireNonNull(other);
        return new SOSPredicateImpl(predicate.or(other.predicate()));
    }

    @Override
    public Predicate predicate() {
        return predicate;
    }

}

package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.sos.interfaces.context.Closure;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClosureImpl implements Closure {

    private Predicate<Asset> predicate;

    public ClosureImpl(Predicate<Asset> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean apply(Asset asset) {
        return predicate.test(asset);
    }

    @Override
    public Closure AND(Closure closure) {
        return new ClosureImpl(predicate.and(closure.getPredicate()));
    }

    @Override
    public Closure OR(Closure closure) {
        return new ClosureImpl(predicate.or(closure.getPredicate()));
    }

    @Override
    public Predicate getPredicate() {
        return predicate;
    }
}

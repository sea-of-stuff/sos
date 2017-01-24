package uk.ac.standrews.cs.sos.model.context.closures;

import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.interfaces.context.Closure;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClosureImpl implements Closure {

    protected SOSAgent agent;
    private Predicate<Asset> predicate;

    protected ClosureImpl(SOSAgent agent, Predicate<Asset> predicate) {
        this.agent = agent;
        this.predicate = predicate;
    }

    @Override
    public boolean test(Asset asset) {
        return predicate.test(asset);
    }

    @Override
    public Closure AND(Closure closure) {
        return new ClosureImpl(agent, this.and(closure));
    }

    @Override
    public Closure OR(Closure closure) {
        return new ClosureImpl(agent, this.or(closure));
    }

}

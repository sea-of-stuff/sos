package uk.ac.standrews.cs.sos.model.context.closures;

import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.interfaces.context.Closure;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BaseClosure implements Closure {

    protected SOSAgent agent;
    private Predicate<Asset> predicate;

    protected BaseClosure(SOSAgent agent, Predicate<Asset> predicate) {
        this.agent = agent;
        this.predicate = predicate;
    }

    @Override
    public boolean test(Asset asset) {
        return predicate.test(asset);
    }

    @Override
    public Closure AND(Closure closure) {
        return new BaseClosure(agent, this.and(closure));
    }

    @Override
    public Closure OR(Closure closure) {
        return new BaseClosure(agent, this.or(closure));
    }

}

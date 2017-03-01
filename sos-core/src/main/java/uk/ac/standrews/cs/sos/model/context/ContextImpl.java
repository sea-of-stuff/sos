package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.interfaces.context.Rule;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
Ø
public class ContextImpl implements Context {

    protected SOSAgent agent;

    private final IGUID guid;
    private final String name;
    Predicate<Asset> predicate;

    public ContextImpl(SOSAgent agent, String name) {
        this.agent = agent;
        this.name = name;

        guid = GUIDFactory.generateRandomGUID();
    }

    public ContextImpl(SOSAgent agent, String name, Predicate<Asset> predicate) {
        this.agent = agent;
        this.name = name;
        this.predicate = predicate;

        guid = GUIDFactory.generateRandomGUID();
    }

    @Override
    public IGUID getGUID() {
        return guid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rule[] getRules() {
        return null;
    }

    @Override
    public boolean test(Asset asset) {
        return predicate.test(asset);
    }

    @Override
    public Context AND(Context context) {
        String newName = name + ".AND." + context.getName();

        return new ContextImpl(agent, newName, this.and(context));
    }

    @Override
    public Context OR(Context context) {
        String newName = name + ".OR." + context.getName();
        return new ContextImpl(agent, newName, this.or(context));
    }

    @Override
    public String toString() {
        return "Context GUID: " + guid + ", Name: " + name;
    }
}

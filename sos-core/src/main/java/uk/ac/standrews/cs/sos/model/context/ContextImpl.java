package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.interfaces.model.ManifestType;
import uk.ac.standrews.cs.sos.interfaces.model.Policy;
import uk.ac.standrews.cs.sos.interfaces.model.SOSPredicate;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextImpl implements Context {

    protected Agent agent;

    private final IGUID guid;
    private final String name;
    protected SOSPredicate predicate;
    protected Node[] sources;

    private static int EMPTY_ARRAY = 0;

    public ContextImpl(Agent agent, String name) {
        this.agent = agent;
        this.name = name;

        // DEFAULT the predicate to return always false.
        // It's not very clean, but this will force subclasses to set the predicate in order to work
        predicate = new SOSPredicateImpl(p -> false);

        guid = GUIDFactory.generateRandomGUID();
    }

    public ContextImpl(Agent agent, String name, SOSPredicate predicate) {
        this.agent = agent;
        this.name = name;
        this.predicate = predicate;

        guid = GUIDFactory.generateRandomGUID();
    }

    @Override
    public ManifestType getType() {
        return ManifestType.CONTEXT;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setSources(Node[] nodes) {
        sources = nodes;
    }

    @Override
    public SOSPredicate predicate() {
        return predicate;
    }

    @Override
    public Policy[] getPolicies() {
        return new Policy[EMPTY_ARRAY];
    }

    @Override
    public Node[] whereToRun() {
        return new Node[EMPTY_ARRAY];
    }

    @Override
    public boolean verifySignature(Identity identity) throws ManifestVerificationException {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public Context AND(Context context) {
        String newName = name + ".AND." + context.getName();
        return new ContextImpl(agent, newName, this.predicate.and(context.predicate()));
    }

    @Override
    public Context OR(Context context) {
        String newName = name + ".OR." + context.getName();
        return new ContextImpl(agent, newName, this.predicate.or(context.predicate()));
    }

    @Override
    public String toString() {
        return "Context GUID: " + guid + ", Name: " + name;
    }
}

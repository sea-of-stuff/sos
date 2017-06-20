package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseContext implements Context {

    protected PolicyActions policyActions;

    protected IGUID guid;
    protected String name;
    protected SOSPredicate predicate;
    protected NodesCollection domain;
    protected NodesCollection codomain;

    private static int EMPTY_ARRAY = 0;
    protected static final long PREDICATE_ALWAYS_TRUE = Long.MAX_VALUE;
    protected static final long PREDICATE_ALWAYS_TO_COMPUTE = 0;

    /**
     * Use this constructor when creating a new context object and its GUID is unknown yet
     *
     * @param policyActions
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContext(PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {
        this(policyActions, GUIDFactory.generateRandomGUID(), name, domain, codomain);
    }

    /**
     * Use this constructor when creating an already existing object with its GUID known already
     *
     * @param policyActions
     * @param guid
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContext(PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {
        this.policyActions = policyActions;

        this.guid = guid;
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
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
    public Policy[] policies() {
        return new Policy[EMPTY_ARRAY];
    }

    @Override
    public NodesCollection whereToRun() {
        return null;
    }

    @Override
    public String toString() {
        return "Context GUID: " + guid + ", Name: " + name;
    }

}

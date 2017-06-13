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

    protected PolicyLanguage policyLanguage;

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
     * @param policyLanguage
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContext(PolicyLanguage policyLanguage, String name, NodesCollection domain, NodesCollection codomain) {
        this(policyLanguage, GUIDFactory.generateRandomGUID(), name, domain, codomain);
    }

    /**
     * Use this constructor when creating an already existing object with its GUID known already
     *
     * @param policyLanguage
     * @param guid
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContext(PolicyLanguage policyLanguage, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {
        this.policyLanguage = policyLanguage;

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
    public SOSPredicate predicate() {
        return new SOSPredicateImpl(guid -> false, PREDICATE_ALWAYS_TRUE);
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

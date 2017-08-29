package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseContext implements Context {

    private JsonNode jsonNode;

    protected PolicyActions policyActions;

    protected IGUID guid;
    protected String name;
    protected SOSPredicate predicate;
    protected NodesCollection domain;
    protected NodesCollection codomain;

    private static int EMPTY_ARRAY = 0;

    /**
     * The predicate is computed once and its result is true forever.
     */
    protected static final long PREDICATE_ALWAYS_TRUE = Long.MAX_VALUE;
    /**
     * The predicate is true only at the time when it is computed.
     */
    protected static final long PREDICATE_ALWAYS_TO_COMPUTE = 0;

    /**
     * Use this constructor when creating a new context object and its GUID is unknown yet
     *
     * @param jsonNode
     * @param policyActions
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContext(JsonNode jsonNode, PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {
        this(jsonNode, policyActions, GUIDFactory.generateRandomGUID(), name, domain, codomain);
    }

    /**
     * Use this constructor when creating an already existing object with its GUID known already
     *
     * @param jsonNode
     * @param policyActions
     * @param guid
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContext(JsonNode jsonNode, PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {
        this.jsonNode = jsonNode;
        this.jsonNode = ((ObjectNode)jsonNode).put("guid", guid.toMultiHash());

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

    // FIXME - rename
    @Override
    public String getName() {
        return name + "-" + guid.toMultiHash();
    }

    @Override
    public NodesCollection domain() {
        return domain;
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
        return jsonNode.toString();
    }

}

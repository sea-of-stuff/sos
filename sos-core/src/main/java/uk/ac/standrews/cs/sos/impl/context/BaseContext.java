package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BaseContext implements Context {

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


    //////////////////////////////////
    // UTILITY - COMMON METHODS //////
    //////////////////////////////////

    /**
     * This method constructs a predicate that checks that the metadata of some given version matches any of the content-types specified
     * @param matchingContentTypes
     * @return
     */
    protected Predicate<IGUID> contentTypePredicate(List<String> matchingContentTypes) {

        SOSAgent agent = SOSAgent.instance();

        return guid -> {

            try {
                String contentType = (String) agent.getMetaProperty(guid, MetadataConstants.CONTENT_TYPE);
                return matchingContentTypes.contains(contentType);

            } catch (Exception e) {
                // This could occur because the metadata could not be found or the type property was not available
                SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
            }

            return false;
        };
    }

}

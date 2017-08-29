package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;

import java.util.Collections;

/**
 * NOTE: This context is used as a reference to code the ContextClassBuilder
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferenceContext extends BaseContext {

    public ReferenceContext(JsonNode jsonNode, PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {
        super(jsonNode, policyActions, name, domain, codomain);
    }

    public ReferenceContext(JsonNode jsonNode, PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {
        super(jsonNode, policyActions, guid, name, domain, codomain);
    }

    @Override
    public SOSPredicate predicate() {

        return new P(PREDICATE_ALWAYS_TRUE);
    }

    class P extends SOSPredicateImpl {

        P(long maxAge) {
            super(maxAge);
        }

        @Override
        public boolean test(IGUID guid) {

            return CommonPredicates.ContentTypePredicate(guid, Collections.singletonList("image/jpeg"));
        }
    }

    @Override
    public Policy[] policies() {
        return new Policy[]{ new CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1) };
    }

}
package uk.ac.standrews.cs.sos.impl.context.examples;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.context.*;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;

import java.util.Collections;

/**
 * This is a context that replicates all binary content to at least three randomly chosen nodes
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BinaryReplicationContext extends BaseContext {

    private static final int NUMBER_OF_REPLICAS = 3;

    public BinaryReplicationContext(JsonNode jsonNode, PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {
        super(jsonNode, policyActions, name, domain, codomain);
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
            return CommonPredicates.ContentTypePredicate(guid, Collections.singletonList("application/octet-stream"));
        }
    }

    @Override
    public Policy[] policies() {

        CommonPolicies.ManifestReplicationPolicy manifestRepl = new CommonPolicies.ManifestReplicationPolicy();
        manifestRepl.factor = NUMBER_OF_REPLICAS;

        return new Policy[]{ manifestRepl };
    }

}

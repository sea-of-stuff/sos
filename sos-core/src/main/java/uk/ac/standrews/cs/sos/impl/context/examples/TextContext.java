package uk.ac.standrews.cs.sos.impl.context.examples;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.context.*;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;

import java.util.Arrays;

/**
 * This is a simple context that categorises all textual content and replicates it at least two times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TextContext extends BaseContext {

    public TextContext(PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {
        super(policyActions, name, domain, codomain);
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
            return CommonPredicates.ContentTypePredicate(guid, Arrays.asList("text", "text/plain", "text/richtext", "text/enriched", "text/html"));
        }
    }

    @Override
    public Policy[] policies() {
        return new Policy[]{
                new CommonPolicies.DeletionPolicy(policyActions, codomain)
        };
    }

}

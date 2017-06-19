package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Collections;

/**
 * NOTE: This context is used as a reference to code the ContextClassBuilder
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferenceContext extends BaseContext {


    public ReferenceContext(PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {
        super(policyActions, name, domain, codomain);
    }

    public ReferenceContext(PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {
        super(policyActions, guid, name, domain, codomain);
    }

    @Override
    public SOSPredicate predicate() {

        SOSAgent agent = SOSAgent.instance();

        return new SOSPredicateImpl(p -> {
            try {
                CommonPredicates.ContentTypePredicate(Collections.singletonList("image/jpeg"));
            } catch (Exception e) {
                SOS_LOG.log(LEVEL.ERROR, "Predicate could not be applied");
            }

            return false;
        }, PREDICATE_ALWAYS_TRUE);
    }

    @Override
    public Policy[] policies() {
        return new Policy[]{  };
    }

}
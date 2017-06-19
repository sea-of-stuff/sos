package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

/**
 * NOTE: This context is used as a reference to code the ContextClassBuilder
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferenceContext extends BaseContext {


    public ReferenceContext(PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {
        super(policyActions, name, domain, codomain);
    }

    public ReferenceContext(PolicyActions policyActions, String name, NodesCollection codomain) {
        super(policyActions, name, new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), codomain);
    }

    public ReferenceContext(PolicyActions policyActions, String name) {
        super(policyActions, name, new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));
    }

    @Override
    public SOSPredicate predicate() {

        SOSAgent agent = SOSAgent.instance();

        return new SOSPredicateImpl(p -> {
            try {

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
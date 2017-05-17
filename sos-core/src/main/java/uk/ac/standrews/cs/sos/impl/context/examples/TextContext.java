package uk.ac.standrews.cs.sos.impl.context.examples;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.BaseContext;
import uk.ac.standrews.cs.sos.impl.context.PolicyLanguage;
import uk.ac.standrews.cs.sos.impl.context.SOSPredicateImpl;
import uk.ac.standrews.cs.sos.model.Manifest;
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

    public TextContext(String name, NodesCollection domain, NodesCollection codomain) {
        super(name, domain, codomain);
    }

    @Override
    public SOSPredicate predicate() {

        return new SOSPredicateImpl(
                contentTypePredicate(Arrays.asList("text", "text/plain", "text/richtext", "text/enriched", "text/html")),
                Long.MAX_VALUE);
    }

    @Override
    public Policy[] policies() {
        return new Policy[]{
                new DeletionPolicy()
        };
    }

    /**
     * Delete content from some nodes
     */
    private class DeletionPolicy implements Policy {

        @Override
        public void apply(Manifest manifest) throws PolicyException {

            try {
                IGUID fakeNodeGUID = GUIDFactory.generateRandomGUID(); // FIXME - have a sensible Node GUID

                boolean hasData = PolicyLanguage.instance().nodeHasData(fakeNodeGUID, manifest.guid());

                if (hasData) {
                    PolicyLanguage.instance().deleteData(manifest.guid(), fakeNodeGUID);
                }

            } catch (SOSException e) {
                throw new PolicyException("Unable to queue/apply policy");
            }
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            try {
                int numberReplicas = PolicyLanguage.instance().numberOfReplicas(null, manifest.guid());
                return numberReplicas == 0;

            } catch (SOSException e) {
                throw new PolicyException("Unable to check if the policy was satisfied");
            }

        }
    }
}

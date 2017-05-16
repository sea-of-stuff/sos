package uk.ac.standrews.cs.sos.impl.context.examples;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.context.BaseContext;
import uk.ac.standrews.cs.sos.impl.context.PolicyLanguage;
import uk.ac.standrews.cs.sos.impl.context.SOSPredicateImpl;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

/**
 * This is a simple context that categorises all textual content and replicates it at least two times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TextContext extends BaseContext {

    private static final int NUMBER_OF_REPLICAS = 3;

    public TextContext(String name) {
        super(name);
    }

    public TextContext(String name, Node[] sources) {
        super(name, sources);
    }

    public TextContext(IGUID guid, String name, Node[] sources) {
        super(guid, name, sources);
    }

    @Override
    public SOSPredicate predicate() {

        return new SOSPredicateImpl(guid -> {

            SOSAgent agent = SOSAgent.instance();

            try {
                String contentType = getMetaProperty(agent, guid, MetadataConstants.CONTENT_TYPE);
                return isText(contentType);

            } catch (Exception e) {
                // This could occur because the metadata could not be found or the type property was not available
                SOS_LOG.log(LEVEL.WARN, "Predicate could not be run");
            }

            return false;
        }, Long.MAX_VALUE);
    }

    private boolean isText(String contentType) {

        switch(contentType.toLowerCase()) {
            case "text":
            case "text/plain":
            case "text/richtext":
            case "text/enriched":
            case "text/html":
                return true;
            default:
                return false;
        }

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
        public boolean run(Manifest manifest) {

            try {
                IGUID fakeNodeGUID = GUIDFactory.generateRandomGUID(); // FIXME - have a sensible Node GUID

                boolean hasData = PolicyLanguage.instance().nodeHasData(fakeNodeGUID, manifest.guid());

                if (hasData) {
                    PolicyLanguage.instance().deleteData(manifest.guid(), fakeNodeGUID);
                }

                return true;
            } catch (SOSException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public boolean check(Manifest manifest) {

            try {
                int numberReplicas = PolicyLanguage.instance().numberOfReplicas(null, manifest.guid());
                return numberReplicas == 0;

            } catch (SOSException e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}

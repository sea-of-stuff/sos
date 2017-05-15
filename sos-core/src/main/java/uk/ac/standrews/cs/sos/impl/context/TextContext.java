package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.SOSPredicate;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Iterator;

/**
 * This is a simple context that categorises all textual content and replicates it at least two times
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TextContext extends CommonContext {

    @Override
    public SOSPredicate predicate() {

        return new SOSPredicateImpl(guid -> {

            SOSAgent agent = SOSAgent.instance();

            try {
                String contentType = getMetaProperty(agent, guid, MetadataConstants.CONTENT_TYPE);
                return isText(contentType);

            } catch (Exception e) {
                SOS_LOG.log(LEVEL.ERROR, "Predicate could not be run");
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
                new ManifestReplicationPolicy(3)
        };
    }

    private class ManifestReplicationPolicy implements Policy {

        private int factor;

        public ManifestReplicationPolicy(int factor) {
            this.factor = factor;
        }

        @Override
        public void run(Manifest manifest) {

            // TODO - use policy language

            try {
                Iterator<Node> nodes = null; // FIXME

                ManifestReplication replication = new ManifestReplication(manifest, nodes, factor, null);
                TasksQueue.instance().performAsyncTask(replication);

            } catch (SOSProtocolException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean check() {

            // TODO - get nodes that might have manifest from DDS
            // challenge nodes

            return false;
        }
    }
}

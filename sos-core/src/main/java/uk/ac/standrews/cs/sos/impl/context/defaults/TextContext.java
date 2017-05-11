package uk.ac.standrews.cs.sos.impl.context.defaults;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.context.CommonContext;
import uk.ac.standrews.cs.sos.impl.context.SOSPredicateImpl;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.model.*;
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

        // Get this node's agent
        SOSAgent agent = SOSAgent.instance();

        return new SOSPredicateImpl(guid -> {
            try {
                Version version = (Version) agent.getManifest(guid);

                Metadata metadata = agent.getMetadata(version.getMetadata());
                String contentType = metadata.getPropertyAsString(MetadataConstants.CONTENT_TYPE);
                return isText(contentType);

            } catch (Exception e) {
                SOS_LOG.log(LEVEL.ERROR, "Predicate could not be run");
            }

            return false;
        });
    }

    @Override
    public Policy[] policies() {
        return new Policy[]{
                new ExamplePolicy(3, true)
        };
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

    private class ExamplePolicy implements Policy {

        private int factor;
        private boolean manifestOnly;

        public ExamplePolicy(int factor, boolean manifestOnly) {
            this.factor = factor;
            this.manifestOnly = manifestOnly;
        }

        @Override
        public void run(Manifest manifest) {

            try {
                Iterator<Node> nodes = null;

                ManifestReplication replication = new ManifestReplication(manifest, nodes, factor, null);
                TasksQueue.instance().performAsyncTask(replication);

                if (!manifestOnly) {
                    // TODO - replicate data too
                    SOS_LOG.log(LEVEL.INFO, "TODO - SHOULD REPLICATE DATA");
                }
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

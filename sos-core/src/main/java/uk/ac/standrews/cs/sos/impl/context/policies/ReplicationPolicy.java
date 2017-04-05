package uk.ac.standrews.cs.sos.impl.context.policies;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.PolicyComputationType;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReplicationPolicy implements Policy {

    private int factor;
    private boolean manifestOnly;

    public ReplicationPolicy(int factor, boolean manifestOnly) {
        this.factor = factor;
        this.manifestOnly = manifestOnly;
    }

    @Override
    public void run(Manifest manifest) {

        try {
            Iterator<Node> nodes = null; // TODO - get nodes

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

    @Override
    public PolicyComputationType computationType() {
        return PolicyComputationType.PERIODICALLY;
    }
}

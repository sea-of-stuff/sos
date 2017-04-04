package uk.ac.standrews.cs.sos.model.context.policies;

import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.Policy;
import uk.ac.standrews.cs.sos.interfaces.model.PolicyComputationType;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;

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

            // TODO - replicate data too
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

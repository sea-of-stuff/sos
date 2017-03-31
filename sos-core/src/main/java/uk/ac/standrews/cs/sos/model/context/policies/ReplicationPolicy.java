package uk.ac.standrews.cs.sos.model.context.policies;

import uk.ac.standrews.cs.sos.actors.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.Policy;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.tasks.TasksQueue;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReplicationPolicy implements Policy {

    private int factor;

    public ReplicationPolicy(int factor) {
        this.factor = factor;
    }

    @Override
    public void run(Manifest manifest) {

        try {
            Iterator<Node> nodes = null; // TODO - get nodes

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

    @Override
    public int computationType() {
        return 0;
    }
}

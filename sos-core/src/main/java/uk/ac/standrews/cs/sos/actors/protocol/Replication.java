package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.io.InputStream;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Replication {

    public static void ReplicateData(InputStream data, Set<Node> nodes) {
        // TODO
    }

    public static void TransferData(InputStream data, Node endpoint) {

        Runnable replicator = () -> {
//
//            try {
//                atomStorage.persistAtomToRemote(endpoint, data, bundles);
//            } catch (StorageException e) {
//                e.printStackTrace();
//            }

        };

        replicator.run();
    }

}

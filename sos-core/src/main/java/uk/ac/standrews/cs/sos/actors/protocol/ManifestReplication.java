package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.*;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplication {

    public static ExecutorService Replicate(Manifest manifest, Set<Node> nodes, DDS dds) throws SOSProtocolException {

        if (dds == null) {
            throw new SOSProtocolException("DDS is null. Manifest replication process is aborted.");
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        nodes.stream()
                .filter(Node::isDDS)
                .distinct()
                .forEach(n -> {
                    Runnable runnable = transferManifest(manifest, n, dds);
                    executor.submit(runnable);
                });

        return executor;
    }

    private static Runnable transferManifest(Manifest manifest, Node node, DDS dds) {

        Runnable replicator = () -> {
            boolean transferWasSuccessful = TransferManifestRequest(manifest, node);

            if (transferWasSuccessful) {
                dds.addManifestDDSMapping(manifest.guid(), node.getNodeGUID());
            }
        };

        return replicator;
    }

    public static boolean TransferManifestRequest(Manifest manifest, Node node) {

        try {
            URL url = SOSEP.DDS_POST_MANIFEST(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(manifest.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            return response.getCode() == HTTPStatus.CREATED;
        } catch (IOException | SOSURLException e) {
            e.printStackTrace();
        }

        return false;
    }

}

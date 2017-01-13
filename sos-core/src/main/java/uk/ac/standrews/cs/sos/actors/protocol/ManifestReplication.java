package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplication {

    public static ExecutorService Replicate(Manifest manifest, Iterator<Node> nodes, int replicationFactor, DDS dds) throws SOSProtocolException {

        if (dds == null) {
            throw new SOSProtocolException("DDS is null. Manifest replication process is aborted.");
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable runnable = transferManifest(manifest, nodes, replicationFactor, dds);
        executor.submit(runnable);

        return executor;
    }

    private static Runnable transferManifest(Manifest manifest, Iterator<Node> nodes, int replicationFactor, DDS dds) {

        Runnable replicator = () -> {

            int successfulReplicas = 0;
            while(nodes.hasNext() || successfulReplicas < replicationFactor) {
                Node node = nodes.next();

                if (node.isDDS()) {
                    boolean transferWasSuccessful = TransferManifestRequest(manifest, node);

                    if (transferWasSuccessful) {
                        SOS_LOG.log(LEVEL.INFO, "Manifest with GUID " + manifest.guid() + " replicated successfully to node: " + node.toString());
                        dds.addManifestDDSMapping(manifest.guid(), node.getNodeGUID());
                        successfulReplicas++;
                    } else {
                        SOS_LOG.log(LEVEL.ERROR, "Unable to replicate Manifest with GUID " + manifest.guid() + " to node: " + node.toString());
                    }
                }
            }

        };

        return replicator;
    }

    private static boolean TransferManifestRequest(Manifest manifest, Node node) {

        try {
            URL url = SOSURL.DDS_POST_MANIFEST(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(manifest.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            boolean transferWasSuccessful = response.getCode() == HTTPStatus.CREATED;

            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.

            return transferWasSuccessful;
        } catch (IOException | SOSURLException e) {
            SOS_LOG.log(LEVEL.ERROR, "TransferManifestRequest failed for manifest " + manifest.guid() + " and node " + node.toString());
        }

        return false;
    }

}

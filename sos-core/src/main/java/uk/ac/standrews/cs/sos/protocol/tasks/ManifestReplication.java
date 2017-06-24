package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.DataDiscoveryService;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.network.HTTPMethod;
import uk.ac.standrews.cs.sos.impl.network.HTTPStatus;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.network.SyncRequest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * The ManifestReplication task, as the name suggests, replicates a manifest to other nodes.
 * The manifest can be replicated only to DataDiscoveryServices (DDS).
 * In doing the replication the caller MUST also specify a wished replication factor for the manifest.
 *
 * If the manifest is successfully replicated to a DDS node:
 * - the local DDS is informed that now that such a manifest is now stored in that node too
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplication extends Task {

    private Manifest manifest;
    private Iterator<Node> nodes;
    private int replicationFactor;
    private DataDiscoveryService dataDiscoveryService;

    public ManifestReplication(Manifest manifest, Iterator<Node> nodes, int replicationFactor, DataDiscoveryService dataDiscoveryService) throws SOSProtocolException {

        if (dataDiscoveryService == null) {
            throw new SOSProtocolException("DDS is null. Manifest replication process is aborted.");
        }

        this.manifest = manifest;
        this.nodes = nodes;
        this.replicationFactor = replicationFactor;
        this.dataDiscoveryService = dataDiscoveryService;
    }

    @Override
    public void performAction() {

        int successfulReplicas = 0;
        while(nodes.hasNext() && successfulReplicas < replicationFactor) {
            Node node = nodes.next();

            if (node.isDDS()) {
                boolean transferWasSuccessful = TransferManifestRequest(manifest, node);

                if (transferWasSuccessful) {
                    SOS_LOG.log(LEVEL.INFO, "Manifest with GUID " + manifest.guid() + " replicated successfully to node: " + node.toString());
                    dataDiscoveryService.addManifestDDSMapping(manifest.guid(), node.getNodeGUID());
                    successfulReplicas++;
                } else {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to replicate Manifest with GUID " + manifest.guid() + " to node: " + node.toString());
                }
            }
        }
    }

    private static boolean TransferManifestRequest(Manifest manifest, Node node) {

        try {
            URL url = SOSURL.DDS_POST_MANIFEST(node);
            SyncRequest request = new SyncRequest(HTTPMethod.POST, url);
            request.setJSONBody(manifest.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            boolean transferWasSuccessful = response.getCode() == HTTPStatus.CREATED;

            try(InputStream ignored = response.getBody()) {} // Ensure that the connection is closed properly.

            return transferWasSuccessful;
        } catch (IOException | SOSURLException e) {
            SOS_LOG.log(LEVEL.ERROR, "TransferManifestRequest failed for manifest " + manifest.guid() + " and node " + node.toString());
        }

        return false;
    }

    @Override
    public String toString() {
        return "ManifestReplication for manifest " + manifest.guid();
    }

}

package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.network.*;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
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
    private NodesCollection nodesCollection;
    private int replicationFactor;
    private NodeDiscoveryService nodeDiscoveryService;
    private DataDiscoveryService dataDiscoveryService;

    // TODO - replication by delegation. See DataReplication!
    public ManifestReplication(Manifest manifest, NodesCollection nodesCollection, int replicationFactor, NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService) throws SOSProtocolException {

        if (dataDiscoveryService == null || nodeDiscoveryService == null) {
            throw new SOSProtocolException("DDS and/or NDS are null. Manifest replication process is aborted.");
        }

        this.nodeDiscoveryService = nodeDiscoveryService;
        this.dataDiscoveryService = dataDiscoveryService;

        this.manifest = manifest;
        this.nodesCollection = nodesCollection;
        this.replicationFactor = replicationFactor;
    }

    @Override
    public void performAction() {

        int successfulReplicas = 0;
        Iterator<IGUID> nodeRefs = nodesCollection.nodesRefs().iterator();
        while(nodeRefs.hasNext() && successfulReplicas < replicationFactor) {

            IGUID ref = nodeRefs.next();
            try {
                Node node = nodeDiscoveryService.getNode(ref);
                if (!node.isDDS()) continue;

                boolean transferWasSuccessful = TransferManifestRequest(manifest, node);

                if (transferWasSuccessful) {
                    SOS_LOG.log(LEVEL.INFO, "Manifest with GUID " + manifest.guid() + " replicated successfully to node: " + node.getNodeGUID().toMultiHash());
                    dataDiscoveryService.addManifestDDSMapping(manifest.guid(), ref);
                    successfulReplicas++;
                } else {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to replicate Manifest with GUID " + manifest.guid() + " to node: " + node.getNodeGUID().toMultiHash());
                }

            } catch (NodeNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to get node with ref: " + ref.toMultiHash());
            }

        }
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }

    private static boolean TransferManifestRequest(Manifest manifest, Node node) {

        try {
            URL url = SOSURL.DDS_POST_MANIFEST(node);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.POST, url, ResponseType.JSON);
            request.setJSONBody(manifest.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            boolean transferWasSuccessful = response.getCode() == HTTPStatus.CREATED;

            try(InputStream ignored = response.getBody()) {} // Ensure that the connection is closed properly.

            return transferWasSuccessful;
        } catch (IOException | SOSURLException e) {
            SOS_LOG.log(LEVEL.ERROR, "TransferManifestRequest failed for manifest " + manifest.guid() + " and node " + node.getNodeGUID().toMultiHash());
        }

        return false;
    }

    @Override
    public String toString() {
        return "ManifestReplication for manifest " + manifest.guid();
    }

}

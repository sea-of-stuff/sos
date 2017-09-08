package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.impl.network.*;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.protocol.json.DataPackage;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * The DataReplication task, as the name suggests, replicates data to other nodes.
 * The data can be replicated only to Storage nodes.
 * In doing the replication the caller MUST also specify a wished replication factor for the data.
 *
 *
 * Input for the DataReplication task:
 * - data to be replicated
 * - list of nodes where the data CAN be replicated
 * - replication factor to satisfy
 *
 * Additionally, the task needs access to the following SOS local services:
 * - storage
 * - node discovery
 * - data discovery
 *
 *
 * When data is replicated successfully to a node
 * - the storage service is informed about the new location for the data
 *
 *
 * If the data is successfully replicated to a Storage node:
 * - TODO - write down how the internal data structures change
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplication extends Task {

    private Data data;
    private NodesCollection nodesCollection;
    private int replicationFactor;
    private boolean delegateReplication;

    private Storage storage;
    private NodeDiscoveryService nodeDiscoveryService;

    /**
     * Replicates the data at least n times within the specified nodes collection.
     * If the delegateReplication parameter is true, then the data is sent to a storage node (within the nodes collection),
     * which will take care of replicating the data on behalf of this node. This storage node will replicate the data
     * to the nodes specified in the nodesCollection.
     *
     *
     * Construct the data replication task.
     * The data, nodes and replication factor paramters are needed to carry out the task
     * The index, nds and dds are needed to promptly update this node about the new replicated content
     *
     * @param data
     * @param nodesCollection
     * @param replicationFactor
     * @param storage
     * @param nodeDiscoveryService
     * @throws SOSProtocolException
     *
     * FIXME - should tell the other node that GUID to expect
     */
    public DataReplication(Data data, NodesCollection nodesCollection, int replicationFactor, Storage storage, NodeDiscoveryService nodeDiscoveryService, boolean delegateReplication) throws SOSProtocolException {

        if (storage == null || nodeDiscoveryService == null) {
            throw new SOSProtocolException("Index, NDS and/or DDS are null. Data replication process is aborted.");
        }

        this.storage = storage;
        this.nodeDiscoveryService = nodeDiscoveryService;

        this.data = data;
        this.nodesCollection = nodesCollection;
        this.replicationFactor = replicationFactor;
        this.delegateReplication = delegateReplication;
    }

    @Override
    public void performAction() {

        try (final ByteArrayOutputStream baos = IO.InputStreamToByteArrayOutputStream(data.getInputStream())) {

            int successfulReplicas = 0;
            Iterator<IGUID> nodeRefs = nodesCollection.nodesRefs().iterator();
            while (nodeRefs.hasNext() && successfulReplicas < replicationFactor) {

                IGUID ref = nodeRefs.next();
                try (InputStream dataClone = new ByteArrayInputStream(baos.toByteArray())) {

                    Node node = nodeDiscoveryService.getNode(ref);
                    if (!node.isStorage()) continue;

                    boolean transferWasSuccessful = transferDataAndUpdateNodeState(dataClone, node, storage);

                    if (transferWasSuccessful) {
                        successfulReplicas++;
                    }

                } catch (IOException | NodeNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to perform replication at node with ref: " + ref);
                }

            }

        } catch (IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "An exception occurred while replicating data");
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

    /**
     * Transfer a stream of data to a given node and update this node state
     *
     * @param data
     * @param node
     * @param storage
     * @return true if the data was transferred successfully.
     */
    private boolean transferDataAndUpdateNodeState(InputStream data, Node node, Storage storage) {
        SOS_LOG.log(LEVEL.INFO, "Will attempt to replicate data to node: " + node.getNodeGUID().toMultiHash());


        try {
            Atom atom = transferDataRequest(data, node);
            SOS_LOG.log(LEVEL.INFO, "Successful data replication to node " + node.getNodeGUID().toMultiHash());

            for(LocationBundle locationBundle:atom.getLocations()) {
                storage.addLocation(atom.guid(), locationBundle);
            }

        } catch (SOSProtocolException e) {
            SOS_LOG.log(LEVEL.ERROR, e.getMessage());
            return false;
        }

        return true;
    }

    private Atom transferDataRequest(InputStream data, Node node) throws SOSProtocolException {

        try {
            URL url = SOSURL.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.POST, url, ResponseType.JSON);

            DataPackage dataPackage = new DataPackage();
            dataPackage.setData(IO.InputStreamToBase64String(data));

            if (delegateReplication) {
                DataPackage.Metadata metadata = new DataPackage.Metadata();
                metadata.setReplicationFactor(replicationFactor);

                DataPackage.Metadata.ReplicationNodes replicationNodes = new DataPackage.Metadata.ReplicationNodes();
                replicationNodes.setType(nodesCollection.type());
                replicationNodes.setRefs(nodesCollection.nodesRefs()
                        .stream()
                        .map(IKey::toMultiHash)
                        .toArray( String[]::new ));

                metadata.setReplicationNodes(replicationNodes);
                dataPackage.setMetadata(metadata);
            }

            String jsonBody = JSONHelper.JsonObjMapper().writeValueAsString(dataPackage);
            request.setJSONBody(jsonBody);

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            try(InputStream body = response.getBody()) {

                if (response.getCode() == HTTPStatus.CREATED) {

                    Atom atom = JSONHelper.JsonObjMapper().readValue(body, AtomManifest.class);
                    return atom;

                } else {
                    throw new SOSProtocolException("Unable to transfer DATA");
                }
            }

        } catch (IOException | SOSURLException e) {
            throw new SOSProtocolException("Unable to transfer DATA", e);
        }

    }

    @Override
    public String toString() {
        return "DataReplication. Replication " + replicationFactor;
    }
}

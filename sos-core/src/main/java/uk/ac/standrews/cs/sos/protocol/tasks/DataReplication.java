package uk.ac.standrews.cs.sos.protocol.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.constants.SOSConstants;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.network.*;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
 * -
 *
 *
 * If the data is successfully replicated to a Storage node:
 * - TODO - write down how the internal data structures change
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplication extends Task {

    private InputStream data;
    private NodesCollection nodesCollection;
    private int replicationFactor;
    private Storage storage;
    private NodeDiscoveryService nodeDiscoveryService;
    private DataDiscoveryService dataDiscoveryService;

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
     * @param dataDiscoveryService
     * @throws SOSProtocolException
     */
    public DataReplication(InputStream data, NodesCollection nodesCollection, int replicationFactor, Storage storage, NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService, boolean delegateReplication) throws SOSProtocolException {

        if (storage == null || nodeDiscoveryService == null || dataDiscoveryService == null) {
            throw new SOSProtocolException("Index, NDS and/or DDS are null. Data replication process is aborted.");
        }

        this.storage = storage;
        this.nodeDiscoveryService = nodeDiscoveryService;
        this.dataDiscoveryService = dataDiscoveryService;

        this.data = data;
        this.nodesCollection = nodesCollection; // TODO - nodes should be filtered already!
        this.replicationFactor = replicationFactor;
    }

    @Override
    public void performAction() {

        try (final ByteArrayOutputStream baos = IO.InputStreamToByteArrayOutputStream(data)) {

            int successfulReplicas = 0;
            Iterator<IGUID> nodeRefs = nodesCollection.nodesRefs().iterator();
            while (nodeRefs.hasNext() && successfulReplicas < replicationFactor) {

                IGUID ref = nodeRefs.next();
                try (InputStream dataClone = new ByteArrayInputStream(baos.toByteArray())) {

                    Node node = nodeDiscoveryService.getNode(ref);
                    if (!node.isStorage()) continue;

                    boolean transferWasSuccessful = transferDataAndUpdateNodeState(dataClone, node, storage, nodeDiscoveryService, dataDiscoveryService);

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

    /**
     * Transfer a stream of data to a given node and update this node state
     *
     * @param data
     * @param node
     * @param storage
     * @param nodeDiscoveryService
     * @param dataDiscoveryService
     * @return true if the data was transferred successfully.
     */
    private static boolean transferDataAndUpdateNodeState(InputStream data, Node node, Storage storage,
                                                          NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService) {
        SOS_LOG.log(LEVEL.INFO, "Will attempt to replicate data to node: " + node.toString());

        Pair<IGUID, Set<LocationBundle>> tuple;
        try {
            tuple = transferDataRequest(data, node);
            SOS_LOG.log(LEVEL.INFO, "Successful data replication to node " + node.toString());

        } catch (SOSProtocolException e) {
            SOS_LOG.log(LEVEL.ERROR, e.getMessage());
            return false;
        }


        for(LocationBundle locationBundle:tuple.Y()) {
            storage.addLocation(tuple.X(), locationBundle);
        }

        // TODO - where is the atom manifest stored?
//        for(Node ddsNode:tuple.Y().Y()) {
//            try {
//                SOS_LOG.log(LEVEL.DEBUG, "Registering DDSNode: " + ddsNode.toString());
//                nodeDiscoveryService.registerNode(ddsNode, true);
//            } catch (NodeRegistrationException e) {
//                SOS_LOG.log(LEVEL.ERROR, "Error while registering dds node");
//            }
//        }
//
//        for(Node ddsNode:tuple.Y().Y()) {
//            dataDiscoveryService.addManifestDDSMapping(tuple.X(), ddsNode.getNodeGUID());
//        }

        return true;
    }

    private static Pair<IGUID, Set<LocationBundle>> transferDataRequest(InputStream data, Node node) throws SOSProtocolException {

        try {
            URL url = SOSURL.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(HTTPMethod.POST, url, ResponseType.JSON);
            request.setBody(data);

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            return parseResponse(response);

        } catch (IOException | SOSURLException e) {
            throw new SOSProtocolException("Unable to transfer DATA", e);
        }

    }

    private static Pair<IGUID, Set<LocationBundle>> parseResponse(Response response) throws SOSProtocolException {

        if (response.getCode() != HTTPStatus.CREATED) {

            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
            catch (IOException e) {
                throw new SOSProtocolException("Unable to transfer DATA and manage data stream correctly");
            }
            throw new SOSProtocolException("Unable to transfer DATA");

        }

        Pair<IGUID, Pair<Set<LocationBundle>, Set<Node>> > retval;

        try (InputStream body = response.getBody()) {
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);

            return getManifestNode(jsonNode);

        } catch (IOException | GUIDGenerationException e) {
            throw new SOSProtocolException("Unable to parse response from slave replication node", e);
        }
    }

    private static Pair<IGUID, Set<LocationBundle>> getManifestNode(JsonNode manifestNode) throws GUIDGenerationException {
        String stringGUID = manifestNode.get("ContentGUID").textValue();

        IGUID guid = GUIDFactory.recreateGUID(stringGUID);

        JsonNode bundlesNode = manifestNode.get(JSONConstants.KEY_LOCATIONS);
        Set<LocationBundle> bundles = new HashSet<>();
        if (bundlesNode.isArray()) {
            for(final JsonNode bundleNode:bundlesNode) {
                LocationBundle bundle = JSONHelper.JsonObjMapper().convertValue(bundleNode, LocationBundle.class);
                bundles.add(bundle);
            }
        }

        return new Pair<>(guid, bundles);
    }

    private static Set<Node> getDDSInfoFeedback(JsonNode ddsInfo) throws GUIDGenerationException {
        if (ddsInfo == null ||  !ddsInfo.isArray()) {
            return Collections.emptySet();
        }

        Set<Node> ddsNodes = new HashSet<>();
        for(JsonNode entry:ddsInfo) {
            IGUID nodeGUID = GUIDFactory.recreateGUID(entry.get(SOSConstants.GUID).asText());
            String hostname = entry.get(SOSConstants.HOSTNAME).asText();
            int port = entry.get(SOSConstants.PORT).asInt();

            // TODO - what if there is already an entry for this node, but different roles?
            // FIXME - Do not hard code this bit
            // need a way to merge roles or override node?
            Node node = new SOSNode(nodeGUID, hostname, port, false, false, true, false, false, false, false);
            ddsNodes.add(node);
        }

        return ddsNodes;
    }

    @Override
    public String toString() {
        return "DataReplication. Replication " + replicationFactor;
    }
}

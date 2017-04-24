package uk.ac.standrews.cs.sos.protocol.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.DDS;
import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.constants.ManifestConstants;
import uk.ac.standrews.cs.sos.constants.SOSConstants;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.network.HTTPMethod;
import uk.ac.standrews.cs.sos.impl.network.HTTPStatus;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.network.SyncRequest;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.utils.Tuple;

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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplication extends Task {

    private InputStream data;
    private Iterator<Node> nodes;
    private int replicationFactor;
    private LocationsIndex index;
    private NDS nds;
    private DDS dds;

    public DataReplication(InputStream data, Iterator<Node> nodes, int replicationFactor,
                           LocationsIndex index, NDS nds, DDS dds) throws SOSProtocolException {

        if (index == null || nds == null || dds == null) {
            throw new SOSProtocolException("Index, NDS and/or DDS are null. Data replication process is aborted.");
        }

        this.data = data;
        this.nodes = nodes;
        this.replicationFactor = replicationFactor;
        this.index = index;
        this.nds = nds;
        this.dds = dds;
    }

    @Override
    public void performAction() {

        try (final ByteArrayOutputStream baos = IO.InputStreamToByteArrayOutputStream(data)) {

            int successfulReplicas = 0;
            while (nodes.hasNext() && successfulReplicas < replicationFactor) {

                Node node = nodes.next();
                if (node.isStorage()) {
                    try (InputStream dataClone = new ByteArrayInputStream(baos.toByteArray())) {
                        boolean transferWasSuccessful = transferDataAndUpdateNodeState(dataClone, node, index, nds, dds);

                        if (transferWasSuccessful) {
                            successfulReplicas++;
                        }

                    } catch (IOException e) {
                        SOS_LOG.log(LEVEL.ERROR, "Unable to perform replication");
                    }
                }
            }

        } catch (IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "An exception occurred while replicating data");
        }
    }

    /**
     * Transfer a stream of data to a given node and update this node state
     * @param data
     * @param node
     * @param index
     * @param nds
     * @param dds
     * @return true if the data was transferred successfully.
     */
    private static boolean transferDataAndUpdateNodeState(InputStream data, Node node, LocationsIndex index, NDS nds, DDS dds) {
        SOS_LOG.log(LEVEL.INFO, "Will attempt to replicate data to node: " + node.toString());

        Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > tuple;
        try {
            tuple = transferDataRequest(data, node);
        } catch (SOSProtocolException e) {
            SOS_LOG.log(LEVEL.ERROR, e.getMessage());
            return false;
        }

        if (tuple == null) {
            SOS_LOG.log(LEVEL.ERROR, "Error while trasnfering data to other nodes");
            return false;
        }

        SOS_LOG.log(LEVEL.INFO, "Successful data replication to node " + node.toString());
        for(LocationBundle locationBundle:tuple.y.x) {
            index.addLocation(tuple.x, locationBundle);
        }

        for(Node ddsNode:tuple.y.y) {
            try {
                SOS_LOG.log(LEVEL.DEBUG, "Registering DDSNode: " + ddsNode.toString());
                nds.registerNode(ddsNode, true);
            } catch (NodeRegistrationException e) {
                SOS_LOG.log(LEVEL.ERROR, "Error while registering dds node");
            }
        }

        for(Node ddsNode:tuple.y.y) {
            dds.addManifestDDSMapping(tuple.x, ddsNode.getNodeGUID());
        }

        return true;
    }

    private static Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > transferDataRequest(InputStream data, Node node) throws SOSProtocolException {

        Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > retval;

        try {
            URL url = SOSURL.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(HTTPMethod.POST, url);
            request.setBody(data);

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            retval = parseResponse(response);
        } catch (IOException | SOSURLException e) {
            throw new SOSProtocolException("Unable to transfer DATA", e);
        }

        return retval;
    }

    private static Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > parseResponse(Response response) throws SOSProtocolException {

        if (response.getCode() != HTTPStatus.CREATED) {
            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
            catch (IOException e) {
                throw new SOSProtocolException("Unable to transfer DATA and manage data stream correctly");
            }
            throw new SOSProtocolException("Unable to transfer DATA");
        }

        Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > retval;

        try (InputStream body = response.getBody()) {
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);
            JsonNode manifestNode = jsonNode.get(SOSConstants.MANIFEST);
            JsonNode ddsInfo = jsonNode.has(SOSConstants.DDD_INFO) ? jsonNode.get(SOSConstants.DDD_INFO) : null;

            Tuple<IGUID, Set<LocationBundle>> manifestNodeInfo = getManifestNode(manifestNode);
            Set<Node> ddsNodes = getDDSInfoFeedback(ddsInfo);

            retval = new Tuple<>(manifestNodeInfo.x, new Tuple<>(manifestNodeInfo.y, ddsNodes));
        } catch (IOException | GUIDGenerationException e) {
            throw new SOSProtocolException("Unable to parse response from slave replication node", e);
        }

        return retval;
    }

    private static Tuple<IGUID, Set<LocationBundle>> getManifestNode(JsonNode manifestNode) throws GUIDGenerationException {
        String stringGUID = manifestNode.get("ContentGUID").textValue();

        IGUID guid = GUIDFactory.recreateGUID(stringGUID);

        JsonNode bundlesNode = manifestNode.get(ManifestConstants.KEY_LOCATIONS);
        Set<LocationBundle> bundles = new HashSet<>();
        if (bundlesNode.isArray()) {
            for(final JsonNode bundleNode:bundlesNode) {
                LocationBundle bundle = JSONHelper.JsonObjMapper().convertValue(bundleNode, LocationBundle.class);
                bundles.add(bundle);
            }
        }

        return new Tuple<>(guid, bundles);
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

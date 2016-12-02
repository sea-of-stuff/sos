package uk.ac.standrews.cs.sos.actors.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.utils.Tuple;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO - test this function with location, inputstream (file), and bufferedinputstream
 * TODO - test with parallel requests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplication {

    public static ExecutorService Replicate(InputStream data, Set<Node> nodes,
                                            LocationsIndex index, NDS nds, DDS dds) throws SOSProtocolException {

        if (index == null || nds == null || dds == null) {
            throw new SOSProtocolException("Index, NDS and/or DDS are null. Data replication process is aborted.");
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        nodes.stream()
                .filter(Node::isStorage)
                .distinct()
                .forEach(n -> {
                    Runnable runnable = transferData(new BufferedInputStream(data), n, index, nds, dds); // FIXME
                    executor.submit(runnable);
                });

        return executor;
    }

    public static ExecutorService Replicate(BufferedInputStream data, Set<Node> nodes,
                                            LocationsIndex index, NDS nds, DDS dds) throws SOSProtocolException {

        if (index == null || nds == null || dds == null) {
            throw new SOSProtocolException("Index, NDS and/or DDS are null. Data replication process is aborted.");
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        nodes.stream()
                .filter(Node::isStorage)
                .distinct()
                .forEach(n -> {
                    Runnable runnable = transferData(data, n, index, nds, dds);
                    executor.submit(runnable);
                });

        return executor;
    }

    public static ExecutorService Replicate(Location location, Set<Node> nodes,
                                            LocationsIndex index, NDS nds, DDS dds) throws SOSProtocolException {

        if (index == null || nds == null || dds == null) {
            throw new SOSProtocolException("Index, NDS and/or DDS are null. Data replication process is aborted.");
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        nodes.stream()
                .filter(Node::isStorage)
                .distinct()
                .forEach(n -> {
                    Runnable runnable = transferData(location, n, index, nds, dds);
                    executor.submit(runnable);
                });

        return executor;
    }

    private static Runnable transferData(BufferedInputStream data, Node node, LocationsIndex index, NDS nds, DDS dds) {

        Runnable replicator = () -> {
            Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > tuple;
            try {
                tuple = transferDataRequest(data, node);
            } catch (SOSProtocolException e) {
                SOS_LOG.log(LEVEL.ERROR, e.getMessage());
                return;
            }

            if (tuple == null) {
                SOS_LOG.log(LEVEL.ERROR, "Error while trasnfering data to other nodes");
                return;
            }

            for(LocationBundle locationBundle:tuple.y.x) {
                index.addLocation(tuple.x, locationBundle);
            }

            for(Node ddsNode:tuple.y.y) {
                try {
                    nds.registerNode(ddsNode);
                } catch (NodeRegistrationException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Error while registering dds node");
                    return;
                }
            }

            for(Node ddsNode:tuple.y.y) {
                dds.addManifestDDSMapping(tuple.x, ddsNode.getNodeGUID());
            }
        };

        return replicator;
    }

    private static Runnable transferData(Location location, Node node, LocationsIndex index, NDS nds, DDS dds) {

        Runnable replicator = () -> {
            Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > tuple;
            try {
                tuple = transferDataRequest(location, node);
            } catch (SOSProtocolException e) {
                SOS_LOG.log(LEVEL.ERROR, e.getMessage());
                return;
            }

            if (tuple == null) {
                SOS_LOG.log(LEVEL.ERROR, "Error while trasnfering data to other nodes");
                return;
            }

            for(LocationBundle locationBundle:tuple.y.x) {
                index.addLocation(tuple.x, locationBundle);
            }

            for(Node ddsNode:tuple.y.y) {
                try {
                    nds.registerNode(ddsNode);
                } catch (NodeRegistrationException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Error while registering dds node");
                    return;
                }
            }

            for(Node ddsNode:tuple.y.y) {
                dds.addManifestDDSMapping(tuple.x, ddsNode.getNodeGUID());
            }
        };

        return replicator;
    }

    private static Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > transferDataRequest(BufferedInputStream data, Node node) throws SOSProtocolException {

        Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > retval;

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            data.reset();
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(data);

            System.out.println("printing request");
            Response response = RequestsManager.getInstance().playSyncRequest(request);
            System.out.println("0");
            InputStream body = response.getBody();
            System.out.println("1");
            if (request.getRespondeCode() != HTTPStatus.CREATED) {
                System.out.println("2");
                throw new SOSProtocolException("Unable to transfer DATA to node: " + node.getNodeGUID());
            }

            System.out.println("a");
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);
            System.out.println("b");
            JsonNode manifestNode = jsonNode.get(SOSConstants.MANIFEST);
            System.out.println("c");
            JsonNode ddsInfo = jsonNode.has(SOSConstants.DDD_INFO) ? jsonNode.get(SOSConstants.DDD_INFO) : null;
            System.out.println("d");
            Tuple<IGUID, Set<LocationBundle>> manifestNodeInfo  = getManifestNode(manifestNode);
            System.out.println("e");
            Set<Node> ddsNodes = getDDSInfoFeedback(ddsInfo);
            System.out.println("f");
            retval = new Tuple<>(manifestNodeInfo.x, new Tuple<>(manifestNodeInfo.y, ddsNodes));
        } catch (IOException | SOSURLException | GUIDGenerationException e) {
            throw new SOSProtocolException("Unable to transfer DATA. Exception: " + e.getMessage());
        }

        return retval;
    }

    private static Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > transferDataRequest(Location location, Node node) throws SOSProtocolException {

        Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > retval;

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(location.getSource());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            InputStream body = response.getBody();

            if (request.getRespondeCode() != HTTPStatus.CREATED) {
                throw new SOSProtocolException("Unable to transfer DATA to node: " + node.getNodeGUID());
            }

            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);
            JsonNode manifestNode = jsonNode.get(SOSConstants.MANIFEST);
            JsonNode ddsInfo = jsonNode.has(SOSConstants.DDD_INFO) ? jsonNode.get(SOSConstants.DDD_INFO) : null;

            Tuple<IGUID, Set<LocationBundle>> manifestNodeInfo  = getManifestNode(manifestNode);
            Set<Node> ddsNodes = getDDSInfoFeedback(ddsInfo);

            retval = new Tuple<>(manifestNodeInfo.x, new Tuple<>(manifestNodeInfo.y, ddsNodes));
        } catch (IOException | SOSURLException | GUIDGenerationException e) {
            throw new SOSProtocolException("Unable to transfer DATA. Exception: " + e.getMessage());
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
            // need a way to merge roles
            Node node = new SOSNode(nodeGUID, hostname, port, false, true, false, false, false);
            ddsNodes.add(node);
        }

        return ddsNodes;
    }

}

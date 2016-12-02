package uk.ac.standrews.cs.sos.actors.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.node.SOSNode;
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

        try (final ByteArrayOutputStream baos = InputStreamToByteArrayOutputStream(data)){

            nodes.stream() // This can be parallelised if we want to
                    .filter(Node::isStorage)
                    .distinct()
                    .forEach(n -> {
                        InputStream dataClone = new ByteArrayInputStream(baos.toByteArray());
                        Runnable runnable = transferData(dataClone, n, index, nds, dds);
                        executor.submit(runnable);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return executor;
    }

    private static Runnable transferData(InputStream data, Node node, LocationsIndex index, NDS nds, DDS dds) {

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

    private static Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > transferDataRequest(InputStream data, Node node) throws SOSProtocolException {

        Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>> > retval;

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(data);

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
            // Do not hard code this bit
            // need a way to merge roles
            Node node = new SOSNode(nodeGUID, hostname, port, false, true, false, false, false);
            ddsNodes.add(node);
        }

        return ddsNodes;
    }

    private static ByteArrayOutputStream InputStreamToByteArrayOutputStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(input, baos);
        return baos;
    }

}

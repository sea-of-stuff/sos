package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
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
public class MetadataReplication {

    public static ExecutorService Replicate(SOSMetadata manifest, Iterator<Node> nodes, int replicationFactor) throws SOSProtocolException {

        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable runnable = transferMetadata(manifest, nodes, replicationFactor);
        executor.submit(runnable);

        return executor;
    }

    private static Runnable transferMetadata(SOSMetadata metadata, Iterator<Node> nodes, int replicationFactor) {

        Runnable replicator = () -> {

            int successfulReplicas = 0;
            while (nodes.hasNext() || successfulReplicas < replicationFactor) {
                Node node = nodes.next();

                if (node.isDDS()) {

                    try {
                        boolean transferWasSuccessful = TransferMetadataRequest(metadata, node);

                        if (transferWasSuccessful) {
                            SOS_LOG.log(LEVEL.INFO, "Metadata with GUID " + metadata.guid() + " replicated successfully to node: " + node.toString());
                            // TODO - inform dds that replication to particular node was successful
                            // dds.addManifestDDSMapping(manifest.guid(), node.getNodeGUID());
                            successfulReplicas++;
                        } else {
                            SOS_LOG.log(LEVEL.ERROR, "Unable to replicate Metadata with GUID " + metadata.guid() + " to node: " + node.toString());
                        }
                    } catch (GUIDGenerationException e) {
                        SOS_LOG.log(LEVEL.WARN, "Unable to generate GUID for metadata");
                    }

                }
            }
        };

        return replicator;
    }

    private static boolean TransferMetadataRequest(SOSMetadata metadata, Node node) throws GUIDGenerationException {

        try {
            URL url = SOSURL.DDS_POST_METADATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(metadata.toString()); // FIXME - toJson?

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            boolean transferWasSuccessful = response.getCode() == HTTPStatus.CREATED;

            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.

            return transferWasSuccessful;
        } catch (IOException | SOSURLException e) {
            SOS_LOG.log(LEVEL.ERROR, "TransferMetadataRequest failed for metadata " + metadata.guid() + " and node " + node.toString());
        }

        return false;
    }
}
package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.network.HTTPMethod;
import uk.ac.standrews.cs.sos.impl.network.HTTPStatus;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.network.SyncRequest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataReplication extends Task {

    private Metadata metadata;
    private Iterator<Node> nodes;
    private int replicationFactor;

    public MetadataReplication(Metadata metadata, Iterator<Node> nodes, int replicationFactor) throws SOSProtocolException {
        this.metadata = metadata;
        this.nodes = nodes;
        this.replicationFactor = replicationFactor;
    }

    @Override
    public void performAction() {

        int successfulReplicas = 0;
        while (nodes.hasNext() && successfulReplicas < replicationFactor) {
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
    }

    private static boolean TransferMetadataRequest(Metadata metadata, Node node) throws GUIDGenerationException {

        try {
            URL url = SOSURL.MMS_POST_METADATA(node);
            SyncRequest request = new SyncRequest(HTTPMethod.POST, url);
            request.setJSONBody(metadata.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            boolean transferWasSuccessful = response.getCode() == HTTPStatus.CREATED;

            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.

            return transferWasSuccessful;
        } catch (IOException | SOSURLException e) {
            SOS_LOG.log(LEVEL.ERROR, "TransferMetadataRequest failed for metadata " + metadata.guid() + " and node " + node.toString());
        }

        return false;
    }

    @Override
    public String toString() {
        return "MetadataReplication for metadata " + metadata;
    }

}

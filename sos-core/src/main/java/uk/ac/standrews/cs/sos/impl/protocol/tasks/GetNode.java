package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * The GetNode task simply attempts to search info about a specific SOS Node by contacting NDS Nodes in the network.
 * This is done by simply iterating over known NDS nodes for a max of NUMBER_OF_TRIALS times.
 *
 * Not attempt to build a P2P structured network has been done for the moment being.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GetNode extends Task {

    private IGUID nodeId;
    private Iterator<Node> knownNDSNodes;
    private Node foundNode;

    private static int NUMBER_OF_TRIALS = 3;

    public GetNode(IGUID nodeGUID, Iterator<Node> knownNDSNodes) {
        this.nodeId = nodeGUID;
        this.knownNDSNodes = knownNDSNodes;
    }

    @Override
    public void performAction() {

        int trial = 0;
        while(knownNDSNodes.hasNext() && trial < NUMBER_OF_TRIALS) {

            Node node = knownNDSNodes.next();
            trial++;

            try {
                URL url = SOSURL.NDS_GET_NODE(node, nodeId);
                SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
                Response response = RequestsManager.getInstance().playSyncRequest(request);

                foundNode = parseNode(response);

                if (foundNode != null) {
                    break;
                }
            } catch (SOSURLException | IOException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to find node " + nodeId + " by contacting node " + node.toString());
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

    private Node parseNode(Response response) throws IOException {

        if (response.getCode() != HTTPStatus.OK) {
            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
            return null;
        }

        try (InputStream inputStream = response.getBody()){

            String body = IO.InputStreamToString(inputStream);
            return JSONHelper.JsonObjMapper().readValue(body, SOSNode.class);

        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public Node getFoundNode() {
        return foundNode;
    }

    @Override
    public String toString() {
        return "GetNode task for guid: " + nodeId;
    }
}

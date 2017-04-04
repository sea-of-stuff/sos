package uk.ac.standrews.cs.sos.protocol.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.model.Node;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.protocol.SOSConstants;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GetNode extends Task {

    private IGUID nodeId;
    private Iterator<Node> knownNDSNodes;
    private Node foundNode;

    private static int NUMBER_OF_TRIALS = 3;

    public GetNode(IGUID nodeGUID, Iterator<Node> knownNDSNodes) {
        // TODO - perform checks

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

                SyncRequest request = new SyncRequest(Method.GET, url);
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

    private Node parseNode(Response response) throws IOException {

        if (response.getCode() != HTTPStatus.OK) {
            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
            return null;
        }

        Node retval;

        try (InputStream inputStream = response.getBody()){
            String body = IO.InputStreamToString(inputStream);
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);

            IGUID nodeGUID = GUIDFactory.recreateGUID(jsonNode.get(SOSConstants.GUID).asText());
            String hostname = jsonNode.get(SOSConstants.HOSTNAME).asText();
            int port = jsonNode.get(SOSConstants.PORT).asInt();

            // TODO - roles are not managed correctly
            retval = new SOSNode(nodeGUID, hostname, port, false, true, false, false, false, false, false);
        } catch (GUIDGenerationException | IOException e) {
            throw new IOException(e);
        }

        return retval;
    }

    public Node getFoundNode() {
        return foundNode;
    }

    @Override
    public String toString() {
        return "GetNode task for guid: " + nodeId;
    }
}

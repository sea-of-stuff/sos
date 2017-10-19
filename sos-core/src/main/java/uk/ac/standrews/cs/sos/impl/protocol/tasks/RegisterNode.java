package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RegisterNode extends Task {

    private Node node;
    private Node ndsNode;

    public RegisterNode(Node node, Node ndsNode) {
        this.node = node;
        this.ndsNode = ndsNode;
    }

    @Override
    public void performAction() {
        SOS_LOG.log(LEVEL.INFO, "Registering node: " + node.guid().toMultiHash() + " to NDS: " + ndsNode.guid().toMultiHash());

        try {
            URL url = SOSURL.NDS_REGISTER_NODE(ndsNode);
            SyncRequest request = new SyncRequest(ndsNode.getSignatureCertificate(), HTTPMethod.POST, url, ResponseType.JSON);
            request.setJSONBody(node.toString());
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {
                SOS_LOG.log(LEVEL.INFO, "Node " + node.guid().toMultiHash() + " was successfully registered to NDS: " + ndsNode.guid().toMultiHash());
            } else {
                SOS_LOG.log(LEVEL.WARN, "Node " + node.guid().toMultiHash() + " was NOT successfully registered to NDS: " + ndsNode.guid().toMultiHash());
            }

            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.

        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to perform node registration to node " + ndsNode.guid().toMultiHash() );
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

    @Override
    public String toString() {
        return "RegisterNode " + ndsNode.guid().toMultiHash() + " to NDS node " + ndsNode.guid().toMultiHash();
    }
}

package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.network.*;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PingNode extends Task {

    private Node node;
    private String message;
    private Long timestamp;
    private boolean valid;
    private Long latency;

    public PingNode(Node node, String message) {
        this.node = node;
        this.message = message;

        timestamp = 0L;
        valid = false;
    }

    @Override
    public void performAction() {
        SOS_LOG.log(LEVEL.INFO, "Info about node: " + node.getNodeGUID().toMultiHash());

        try {
            URL url = SOSURL.NODE_PING(node, message);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url, ResponseType.TEXT);
            request.setJSONBody(node.toString());

            long startRequest = System.currentTimeMillis();
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {
                String pong = response.getStringBody();
                valid = pong.contains(message);

                try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
            } else {
                valid = false;
            }
            timestamp = System.currentTimeMillis();
            latency = System.currentTimeMillis() - startRequest;

        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to get info about node " + node.getNodeGUID().toMultiHash());
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

    public boolean valid() {
        return valid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "InfoNode " + node.getNodeGUID();
    }

    public Long getLatency() {
        return latency;
    }
}

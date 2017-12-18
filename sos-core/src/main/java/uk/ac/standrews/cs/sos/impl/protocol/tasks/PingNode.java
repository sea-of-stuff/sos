package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
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
public class PingNode extends Task {

    private final Node node;
    private final String message;
    private Long timestamp;
    private boolean valid;
    private Long latency;

    public PingNode(Node node, String message) {
        super();

        this.node = node;
        this.message = message;

        timestamp = 0L;
        valid = false;
    }

    @Override
    public void performAction() {
        SOS_LOG.log(LEVEL.INFO, "Info about node: " + node.guid().toMultiHash());

        try {
            URL url = SOSURL.NODE_PING(node, message);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url, ResponseType.TEXT);

            long startRequest = System.currentTimeMillis();
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {
                String pong = response.getStringBody();
                valid = pong.contains(message);

                try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
                setState(TaskState.SUCCESSFUL);

            } else {
                setState(TaskState.UNSUCCESSFUL);
                valid = false;
            }
            timestamp = System.currentTimeMillis();
            latency = System.currentTimeMillis() - startRequest;

        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to get info about node " + node.guid().toMultiHash());
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
        return "InfoNode " + node.guid();
    }

    public Long getLatency() {
        return latency;
    }
}

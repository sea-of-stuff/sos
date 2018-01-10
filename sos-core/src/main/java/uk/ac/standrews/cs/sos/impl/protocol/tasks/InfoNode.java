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
public class InfoNode extends Task {

    private final Node node;
    private String info;

    public InfoNode(Node node) {
        super();

        this.node = node;
        this.info = "";
    }

    @Override
    public void performAction() {
        SOS_LOG.log(LEVEL.INFO, "InfoNode for: " + node.guid().toMultiHash());

        try {
            URL url = SOSURL.NODE_INFO(node);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url, ResponseType.JSON);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {
                info = response.getJSON().toString();
                SOS_LOG.log(LEVEL.DEBUG, "InfoNode -- " + info);

                try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
                setState(TaskState.SUCCESSFUL);
            } else {
                SOS_LOG.log(LEVEL.DEBUG, "InfoNode -- ERROR RESPONSE");
                setState(TaskState.ERROR);
            }

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

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "InfoNode " + node.guid();
    }
}

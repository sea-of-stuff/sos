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
public class InfoNode extends Task {

    private Node node;
    private String info;

    public InfoNode(Node node) {
        this.node = node;
        this.info = "";
    }

    @Override
    public void performAction() {
        SOS_LOG.log(LEVEL.INFO, "Info about node: " + node.toString());

        try {
            URL url = SOSURL.NODE_INFO(node);
            SyncRequest request = new SyncRequest(HTTPMethod.GET, url, ResponseType.JSON);
            request.setJSONBody(node.toString());
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {
                info = response.getJSON().toString();

                try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
            }

        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to get info about node " + node.toString() );
        }
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "InfoNode " + node.getNodeGUID();
    }
}
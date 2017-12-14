package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Fetch data that matches the entityId from a specified node
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchData extends Task {

    private Node node;
    private IGUID entityId;
    private InputStream body;

    public FetchData(Node node, IGUID entityId) throws IOException {
        super();

        if (!node.isStorage()) {
            setState(TaskState.ERROR);
            throw new IOException("Attempting to fetch data from non-Storage node");
        }

        if (entityId == null || entityId.isInvalid()) {
            setState(TaskState.ERROR);
            throw new IOException("Attempting to fetch data, but you have given an invalid GUID");
        }

        this.node = node;
        this.entityId = entityId;
        this.body = new NullInputStream(0);
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Data will be fetched from node " + node.guid());

        try {
            URL url = SOSURL.STORAGE_GET_DATA(node, entityId);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {
                setState(TaskState.SUCCESSFUL);
                SOS_LOG.log(LEVEL.INFO, "Data fetched successfully from node " + node.guid());
            } else {
                setState(TaskState.UNSUCCESSFUL);
                SOS_LOG.log(LEVEL.WARN, "Data was not fetched successfully from node " + node.guid());
            }

            body = response.getBody();
        } catch(IOException | SOSURLException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Data not fetched successfully from node " + node.guid() + " - Exception: " + e.getMessage());
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

    public InputStream getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "FetchData for guid " + entityId + " from node " + node.guid();
    }
}

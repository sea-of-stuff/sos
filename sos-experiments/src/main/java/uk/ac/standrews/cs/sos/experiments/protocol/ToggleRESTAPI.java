package uk.ac.standrews.cs.sos.experiments.protocol;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ToggleRESTAPI extends Task {

    private final Node node;
    private final boolean disable;

    public ToggleRESTAPI(Node node, boolean disable) {
        super();

        this.node = node;
        this.disable = disable;
    }

    @Override
    protected void performAction() {
        SOS_LOG.log(LEVEL.INFO, "Disabling REST for node: " + node.guid().toMultiHash());

        try {
            URL url = disable ? ExperimentURL.DISABLE_REST(node) : ExperimentURL.ENABLE_REST(node);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url, ResponseType.TEXT);

            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {

                response.consumeResponse();

                if (response.getCode() == HTTPStatus.OK) {
                    setState(TaskState.SUCCESSFUL);
                } else {
                    setState(TaskState.UNSUCCESSFUL);
                }

            } else {
                setState(TaskState.UNSUCCESSFUL);
            }

        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to disable REST for node " + node.guid().toMultiHash());
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
}

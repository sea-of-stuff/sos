package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TriggerPredicate extends Task {

    private Node node;
    private IGUID context;

    public TriggerPredicate(Node node, IGUID context) {
        this.node = node;
        this.context = context;
    }

    @Override
    protected void performAction() {

        try {
            URL url = SOSURL.EXPERIMENT_TRIGGER_PREDICATE(node, context);

            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url, ResponseType.TEXT);
            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                setState(TaskState.ERROR);
                throw new IOException();
            }

            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.
            setState(TaskState.SUCCESSFUL);

        } catch (IOException | SOSURLException e) {
            setState(TaskState.ERROR);
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

package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.impl.protocol.json.DataPackage;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Payload_JSON extends Task {

    private final Node node;
    private final InputStream payload;
    private final boolean sign;
    private Long timestamp;
    private Long latency;

    /**
     *
     * @param node to send payload to
     * @param payload to send
     * @param sign if true the request will be signed if possible. if false, the request will never be signed.
     */
    public Payload_JSON(Node node, InputStream payload, boolean sign) {
        super();

        this.node = node;
        this.payload = payload;
        this.sign = sign;

        timestamp = 0L;
    }

    @Override
    protected void performAction() {

        try {
            URL url = SOSURL.NODE_PAYLOAD_JSON(node);

            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.POST, url, ResponseType.TEXT);

            DataPackage dataPackage = new DataPackage();
            dataPackage.setData(IO.InputStreamToBase64String(payload)); // Data is transformed to base64 as expected by the RE

            String jsonBody = JSONHelper.jsonObjMapper().writeValueAsString(dataPackage);
            request.setJSONBody(jsonBody);

            long startRequest = System.nanoTime();
            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                setState(TaskState.ERROR);
                throw new IOException();
            }

            response.consumeResponse();
            if (response.getCode() == HTTPStatus.OK) {
                setState(TaskState.SUCCESSFUL);
            } else {
                setState(TaskState.UNSUCCESSFUL);
            }

            latency = System.nanoTime() - startRequest;
            timestamp = System.currentTimeMillis();

        } catch (SOSURLException | IOException e) {
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

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getLatency() {
        return latency;
    }
}

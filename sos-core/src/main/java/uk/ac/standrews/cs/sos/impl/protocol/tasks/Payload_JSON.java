/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
     * @param sign if true the request will be signed if possible. if false, the request will never be signed. FIXME
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
            Response response = RequestsManager.getInstance().playSyncRequest(request, sign);
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

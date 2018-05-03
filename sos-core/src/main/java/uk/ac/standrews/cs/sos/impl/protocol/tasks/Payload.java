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
 * NOTE: use this only for testing and experiments
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Payload extends Task {

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
    public Payload(Node node, InputStream payload, boolean sign) {
        super();

        this.node = node;
        this.payload = payload;
        this.sign = sign;

        timestamp = 0L;
    }

    @Override
    public void performAction() {
        SOS_LOG.log(LEVEL.INFO, "Info about node: " + node.guid().toMultiHash());

        try {
            URL url = SOSURL.NODE_PAYLOAD(node);
            SyncRequest request;
            if (sign) {
                request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.POST, url, ResponseType.TEXT);
            } else {
                request = new SyncRequest(HTTPMethod.POST, url, ResponseType.TEXT);
            }
            request.setBody(payload);
            request.setContent_type("application/octet-stream");

            long startRequest = System.nanoTime();
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {

                if (response.getCode() == HTTPStatus.OK) {
                    setState(TaskState.SUCCESSFUL);
                } else {
                    System.out.println("CODE " + response.getCode());
                    setState(TaskState.UNSUCCESSFUL);
                }

                response.consumeResponse();

            } else {
                setState(TaskState.ERROR);
                SOS_LOG.log(LEVEL.ERROR, "Unable to get proper response from node " + node.guid().toMultiHash());
            }

            latency = System.nanoTime() - startRequest;
            timestamp = System.currentTimeMillis();

        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to get response on payload request from node " + node.guid().toMultiHash());
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

    @Override
    public String toString() {
        return "InfoNode " + node.guid();
    }

    public Long getLatency() {
        return latency;
    }
}

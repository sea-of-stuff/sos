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

import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.guid.IGUID;
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
 * Fetch data that matches the entityId from a specified node
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchAtom extends Task {

    private Node node;
    private IGUID entityId;
    private InputStream body;

    public FetchAtom(Node node, IGUID entityId) throws IOException {
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
            URL url = SOSURL.STORAGE_GET_ATOM(node, entityId);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                setState(TaskState.ERROR);
                throw new IOException();
            }

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
        return "FetchAtom for guid " + entityId + " from node " + node.guid();
    }
}

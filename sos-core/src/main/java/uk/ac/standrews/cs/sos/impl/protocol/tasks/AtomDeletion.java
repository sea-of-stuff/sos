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

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.ErrorResponseImpl;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomDeletion extends Task {

    private Node node;
    private IGUID guid;

    public AtomDeletion(Node node, IGUID guid) {
        this.node = node;
        this.guid = guid;
    }

    @Override
    protected void performAction() {

        try {
            URL url = SOSURL.STORAGE_DELETE_ATOM(node, guid);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.DELETE, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {

                response.consumeResponse();
                setState(TaskState.SUCCESSFUL);
            } else {
                SOS_LOG.log(LEVEL.DEBUG, "DataDeletion -- ERROR RESPONSE");
                setState(TaskState.ERROR);
            }

        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to delete data with GUID " + guid.toMultiHash() + " in node " + node.guid().toMultiHash());
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

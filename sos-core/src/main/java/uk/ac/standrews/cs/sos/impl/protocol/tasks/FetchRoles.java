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
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * Fetch the roles of a given user
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchRoles extends Task {

    private Node node;
    private IGUID userid;
    private Set<Role> roles;

    public FetchRoles(Node node, IGUID userid) throws IOException {
        super();

        if (!node.isRMS()) {
            setState(TaskState.ERROR);
            throw new IOException("Attempting to fetch role from non-RMS node");
        }

        this.node = node;
        this.userid = userid;
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Roles for user with GUID " + userid.toMultiHash() + " will be fetched from node " + node.guid().toShortString());

        try {
            URL url = SOSURL.USRO_GET_ROLES(node, userid);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                setState(TaskState.ERROR);
                throw new IOException();
            }

            if (response.getCode() == HTTPStatus.OK) {

                try (InputStream inputStream = response.getBody()) {

                    String responseBody = IO.InputStreamToString(inputStream);

                    // TODO - array of roles...
                    // this.roles = JSONHelper.jsonObjMapper().readValue(responseBody, Role.class);

                    SOS_LOG.log(LEVEL.INFO, "Roles for given user fetched successfully from node " + node.guid());
                    setState(TaskState.SUCCESSFUL);
                }

            } else {
                setState(TaskState.UNSUCCESSFUL);
                SOS_LOG.log(LEVEL.ERROR, "Roles for given user were not fetched successfully from node " + node.guid().toShortString());
                throw new IOException();
            }
        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch roles for given user");
        }
    }

    public Set<Role> getRoles() {
        return roles;
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

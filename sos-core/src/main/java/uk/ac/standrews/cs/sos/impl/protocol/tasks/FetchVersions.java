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

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchVersions extends Task {

    private Node node;
    private IGUID invariant;
    private Set<IGUID> versions;

    public FetchVersions(Node node, IGUID invariant) throws IOException {
        super();

        if (!node.isMDS()) {
            setState(TaskState.ERROR);
            throw new IOException("Attempting to fetch manifest from non-MDS node");
        }

        if (invariant == null || invariant.isInvalid()) {
            setState(TaskState.ERROR);
            throw new IOException("Attempting to fetch manifest, but you have given an invalid GUID");
        }

        this.node = node;
        this.invariant = invariant;
        this.versions = new LinkedHashSet<>();
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Versions for invariant " + invariant.toMultiHash() + " will be fetched from node " + node.guid().toShortString());

        try {
            URL url = SOSURL.MDS_GET_VERSIONS(node, invariant);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                setState(TaskState.ERROR);
                throw new IOException();
            }

            if (response.getCode() == HTTPStatus.OK) {

                try (InputStream inputStream = response.getBody()) {

                    String responseBody = IO.InputStreamToString(inputStream);
                    this.versions = readJSONArrayOfGUIDs(responseBody);
                    SOS_LOG.log(LEVEL.INFO, "Manifest fetched successfully from node " + node.guid());
                    setState(TaskState.SUCCESSFUL);
                }

            } else {
                setState(TaskState.UNSUCCESSFUL);
                SOS_LOG.log(LEVEL.ERROR, "Unable to fetch versions for invariant " + invariant.toMultiHash() + " successfully from node " + node.guid().toShortString());
                throw new IOException();
            }


        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch versions");
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

    public Set<IGUID> getVersions() {
        return versions;
    }

    private Set<IGUID> readJSONArrayOfGUIDs(String json) throws IOException {

        Set<IGUID> retval = new LinkedHashSet<>();

        JsonNode node = JSONHelper.jsonObjMapper().readTree(json);
        for(JsonNode child:node) {

            IGUID guid;
            try {
                guid = GUIDFactory.recreateGUID(child.asText());
            } catch (GUIDGenerationException e) {
                guid = new InvalidID();
            }

            retval.add(guid);
        }

        return retval;
    }
}

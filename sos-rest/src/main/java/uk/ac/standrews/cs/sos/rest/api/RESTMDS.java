/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module rest.
 *
 * rest is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * rest is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rest. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.datamodel.AtomManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.VersionManifest;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.MDSNode;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;

import static uk.ac.standrews.cs.sos.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/mds/")
@MDSNode
public class RESTMDS {

    @POST
    @Path("/manifest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postManifest(String json, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/mds/manifest");

        Manifest manifest;
        try {
            JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(json);
            ManifestType type = ManifestType.get(jsonNode.get(JSONConstants.KEY_TYPE).textValue());
            manifest = getManifest(type, json);
        } catch (IOException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }

        if (manifest == null) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }

        try {
            ManifestsDataService manifestsDataService = RESTConfig.sos.getMDS();
            manifestsDataService.addManifest(manifest);

        } catch (ManifestPersistException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }

        return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, manifest.toString());
    }

    @GET
    @Path("/manifest/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManifest(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/mds/manifest/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID manifestGUID;
        try {
            manifestGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        try {
            ManifestsDataService manifestsDataService = RESTConfig.sos.getMDS();
            Manifest manifest = manifestsDataService.getManifest(manifestGUID);
            return HTTPResponses.OK(RESTConfig.sos, node_challenge, manifest.toString());

        } catch (ManifestNotFoundException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }
    }

    @GET
    @Path("/manifest/guid/{guid}/challenge/{challenge: .*}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManifest(@PathParam("guid") String guid, @PathParam("challenge") final String challenge, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {

        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/mds/manifest/guid/" + guid + "/challenge/" + challenge);

        IGUID manifestGUID;
        try {
            manifestGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        if (challenge.trim().isEmpty()) return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Challenge is empty");

        ManifestsDataService mds = RESTConfig.sos.getMDS();
        IGUID challengeResult = mds.challenge(manifestGUID, challenge);

        return HTTPResponses.OK(RESTConfig.sos, node_challenge, challengeResult.toMultiHash());
    }

    @DELETE
    @Path("/manifest/guid/{guid}")
    public Response deleteManifest(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {

        SOS_LOG.log(LEVEL.INFO, "REST: DELETE /sos/mds/manifest/guid/" + guid);

        IGUID manifestGUID;
        try {
            manifestGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        try {
            ManifestsDataService mds = RESTConfig.sos.getMDS();
            mds.delete(manifestGUID);
            return HTTPResponses.OK(RESTConfig.sos, node_challenge);

        } catch (ManifestNotFoundException e) {
            SOS_LOG.log(LEVEL.ERROR, "REST: DELETE  /sos/mds/manifest/guid/{guid}");
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

    @POST
    @Path("/compound/data")
    public Response makeCompoundData() {

        // return storage node that should be used to upload the data
        // then should make a call to /compound/data/finalise (containing json of all components)
        // storage node should have the "freedom" to delete the data until finalise call
        return null;
    }

    @GET
    @Path("/versions/invariant/{invariant}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getVersions(@PathParam("invariant") String invariant, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/mds/versions/invariant/{invariant}");


        if (invariant == null || invariant.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID manifestGUID;
        try {
            manifestGUID = GUIDFactory.recreateGUID(invariant);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }


        ManifestsDataService manifestsDataService = RESTConfig.sos.getMDS();
        Set<IGUID> versions = manifestsDataService.getVersions(manifestGUID);

        ArrayNode arrayNode = JSONHelper.jsonObjMapper().createArrayNode();
        for(IGUID version:versions) {
            arrayNode.add(version.toMultiHash());
        }

        return HTTPResponses.OK(RESTConfig.sos, node_challenge, arrayNode.toString());
    }

    private Manifest getManifest(ManifestType type, String json) throws IOException {
        Manifest manifest;
        switch(type) {
            case ATOM:
                manifest = getAtomManifest(json);
                break;
            case COMPOUND:
                manifest = getCompoundManifest(json);
                break;
            case VERSION:
                manifest = getVersionManifest(json);
                break;
            default:
                return null;
        }

        return manifest;
    }

    private AtomManifest getAtomManifest(String json) throws IOException {
        return JSONHelper.jsonObjMapper().readValue(json, AtomManifest.class);
    }

    private CompoundManifest getCompoundManifest(String json) throws IOException {
        return JSONHelper.jsonObjMapper().readValue(json, CompoundManifest.class);
    }

    private VersionManifest getVersionManifest(String json) throws IOException {
        return JSONHelper.jsonObjMapper().readValue(json, VersionManifest.class);
    }
}

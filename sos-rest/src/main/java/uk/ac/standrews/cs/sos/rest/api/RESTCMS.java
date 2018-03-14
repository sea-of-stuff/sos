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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.impl.json.CommonJson;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.CMSNode;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_GUID;
import static uk.ac.standrews.cs.sos.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/cms/")
@CMSNode
public class RESTCMS {

    @GET
    @Path("/contexts")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getAllContext(@HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/cms/contexts");

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            Set<IGUID> contexts = contextService.getContexts();

            ArrayNode jsonArray = CommonJson.GUIDSetToJsonArray(contexts);
            String output = JSONHelper.jsonObjMapper().writeValueAsString(jsonArray);

            return HTTPResponses.OK(RESTConfig.sos, node_challenge, output);
        }  catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

    @POST
    @Path("/context")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response add(String context, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/cms/context");

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            // TODO - do not add context if definition has not changed

            // This is a No-Merge policy
            // calculate invariant of context without adding it to node
            // if invariant is already in node, then do not add it.
            IGUID guid = contextService.addContext(context);
            SOS_LOG.log(LEVEL.INFO, "Added context with GUID: " + guid.toMultiHash());

            ObjectNode objectNode = JSONHelper.jsonObjMapper().createObjectNode();
            objectNode.put(KEY_GUID, guid.toMultiHash());

            return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, objectNode.toString());
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

    @GET
    @Path("/context/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByGUID(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/cms/context/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID contextGUID;
        try {
            contextGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            Context context = contextService.getContext(contextGUID);

            return HTTPResponses.OK(RESTConfig.sos, node_challenge, context.toString());

        } catch (ContextNotFoundException e) {
            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Unable to find context with GUID " + contextGUID.toMultiHash());
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

    @GET
    @Path("/context/guid/{guid}/contents")
    @Produces({MediaType.TEXT_PLAIN})
    public Response findContextContents(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/cms/context/guid/{guid}/contents");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID contextGUID;
        try {
            contextGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            Set<IGUID> contents = contextService.getContents(contextGUID);
            String output = StringUtils.join(contents, ",\n");

            return HTTPResponses.OK(RESTConfig.sos, node_challenge, output);
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

    @DELETE
    @Path("/context/invariant/{guid}")
    public Response deleteContextVersions(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: DELETE /sos/cms/context/invariant/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID contextInvariantGUID;
        try {
            contextInvariantGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            contextService.deleteContext(contextInvariantGUID);

            return HTTPResponses.OK(RESTConfig.sos, node_challenge);

        } catch (ContextNotFoundException e) {
            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Unable to find context with Invariant GUID " + contextInvariantGUID.toMultiHash());
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }
}

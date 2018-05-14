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

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.ExperimentNode;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static uk.ac.standrews.cs.sos.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * REST interfaces needed only when running experiments.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/experiment/")
@ExperimentNode
public class RESTExperiment {

    @GET
    @Path("/cms/guid/{guid}/predicate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response triggerPredicateOfContext(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/experiment/guid/{guid}/predicate");
        System.out.println("REST API - Triggering predicate");

        if (guid == null || guid.isEmpty()) {
            SOS_LOG.log(LEVEL.ERROR, "REST: GET /sos/experiment/guid/{guid}/predicate - Bad input (no context guid)");
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID contextGUID;
        try {
            contextGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            SOS_LOG.log(LEVEL.ERROR, "REST: GET /sos/experiment/guid/{guid}/predicate - Bad input (malformed context guid)");
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            int assetsProcessed = contextService.runContextPredicateNow(contextGUID); // Returns when predicate is run for all assets.

            return HTTPResponses.OK(RESTConfig.sos, node_challenge, assetsProcessed);

        } catch (ContextNotFoundException e) {
            SOS_LOG.log(LEVEL.ERROR, "REST: GET /sos/experiment/guid/{guid}/predicate - Context not found. GUID: " + contextGUID.toMultiHash());
            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Unable to find context with GUID " + contextGUID.toMultiHash());
        } catch (Exception e) {
            SOS_LOG.log(LEVEL.ERROR, "REST: GET /sos/experiment/guid/{guid}/predicate - Internal server error");
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }


    @GET
    @Path("/rest/disable")
    @Produces(MediaType.TEXT_PLAIN)
    public Response disabledREST(@HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/experiment/rest/disable");
        System.out.println("REST API - Disabled");

        RESTConfig.sos.setRestEnabled(false);
        return HTTPResponses.OK(RESTConfig.sos, node_challenge);
    }

    @GET
    @Path("/rest/enable")
    @Produces(MediaType.TEXT_PLAIN)
    public Response enableREST(@HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/experiment/rest/enable");
        System.out.println("REST API - Enabled");

        RESTConfig.sos.setRestEnabled(true);
        return HTTPResponses.OK(RESTConfig.sos, node_challenge);
    }

    @DELETE
    @Path("/atoms")
    public Response deleteAtoms() {
        SOS_LOG.log(LEVEL.INFO, "REST: DELETE /sos/experiment/atoms");
        System.out.println("REST API - Delete all atoms");

        try {
            RESTConfig.sos.getLocalStorage().deleteAtomsDirectory();
            return HTTPResponses.OK(RESTConfig.sos, null);
        } catch (DataStorageException e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, null);
        }

    }
}

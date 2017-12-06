package uk.ac.standrews.cs.sos.rest.api;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
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
        SOS_LOG.log(LEVEL.INFO, "REST: POST /guid/{guid}/predicate");

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
            contextService.runContextPredicateNow(contextGUID); // Returns when predicate is run for all assets.

            return HTTPResponses.OK(RESTConfig.sos, node_challenge);
        } catch (ContextNotFoundException e) {

            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Unable to find context with GUID " + contextGUID.toMultiHash());
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }
}

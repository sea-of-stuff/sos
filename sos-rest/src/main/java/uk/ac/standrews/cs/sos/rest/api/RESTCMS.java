package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
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
            Set<IGUID> contexts = contextService.getContextsRefs();

            ArrayNode jsonArray = CommonJson.GUIDSetToJsonArray(contexts);
            String output = JSONHelper.JsonObjMapper().writeValueAsString(jsonArray);

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
            IGUID guid = contextService.addContext(context);

            return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, guid.toMultiHash());
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }

    }

    @GET
    @Path("/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByGUID(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/cms/guid/{guid}");

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
}

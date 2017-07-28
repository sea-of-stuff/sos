package uk.ac.standrews.cs.sos.rest;

import org.apache.commons.lang3.StringUtils;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.CMSNode;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/cms/")
@CMSNode
public class RESTCMS {

    @GET
    @Path("/contexts")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getAllContext() {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /contexts");

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            Set<Context> contexts = contextService.getContexts();
            String output = StringUtils.join(contexts, ",\n");

            return HTTPResponses.OK(output);
        }  catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER();
        }
    }

    @POST
    @Path("/context")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response add(String context) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /cms/context");

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            IGUID guid = contextService.addContext(context);

            return HTTPResponses.OK(guid.toString());
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @GET
    @Path("/context/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByGUID(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /context/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID contextGUID;
        try {
            contextGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            Context context = contextService.getContext(contextGUID);

            return HTTPResponses.OK(context.toString());
        } catch (ContextNotFoundException e) {

            return HTTPResponses.NOT_FOUND("Unable to find context with GUID " + contextGUID.toString());
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER();
        }
    }

    @GET
    @Path("/context/guid/{guid}/contents")
    @Produces({MediaType.TEXT_PLAIN})
    public Response findContextContents(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /context/guid/{guid}/contents");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID contextGUID;
        try {
            contextGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            Set<IGUID> contents = contextService.getContents(contextGUID);
            String output = StringUtils.join(contents, ",\n");

            return HTTPResponses.OK(output);
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER();
        }
    }
}

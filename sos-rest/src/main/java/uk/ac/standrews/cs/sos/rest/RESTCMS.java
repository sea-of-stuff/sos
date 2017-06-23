package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.actors.ContextService;
import uk.ac.standrews.cs.sos.bindings.CMSNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/cms/")
@CMSNode
public class RESTCMS {

    @POST
    @Path("/context")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response register(String context) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /cms/context");

        try {
            ContextService contextService = RESTConfig.sos.getCMS();
            contextService.addContext(context);

            return HTTPResponses.OK();
        } catch (Exception e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }
}

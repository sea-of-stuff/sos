package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.bindings.DDSNode;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/dds/")
@DDSNode
public class RESTDDS {

    @POST
    @Path("/manifest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postManifest(Manifest manifest) {

        return HTTPResponses.CREATED("hello");
    }

    @GET
    @Path("/manifest/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManifest(@PathParam("guid") String guid) {

        return HTTPResponses.OK("Test");
    }
}

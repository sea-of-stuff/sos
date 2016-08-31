package uk.ac.standrews.cs.sos.rest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * @see https://quicksilver.host.cs.st-andrews.ac.uk/sos/api.html#node-discovery-service
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/nds/")
public class RESTNDS {

    @PUT
    @Path("/register")
    public void register() {}

    @GET
    @Path("/guid/{guid}")
    public void findByGUID() {}

    @GET
    @Path("/guid/{guid}/roles")
    public void getRoles() {}

    @GET
    @Path("/role/{role}")
    public void findByRole() {}
}

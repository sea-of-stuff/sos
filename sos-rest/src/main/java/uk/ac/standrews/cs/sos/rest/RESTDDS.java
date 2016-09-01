package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.bindings.DDSNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/dds/")
@DDSNode
public class RESTDDS {

    @GET
    @Path("/{param}")
    public Response dummy(@PathParam("param") String msg) {
        if (msg == null || msg.isEmpty()) {
            msg = "What? Please give me a message.";
        }

        String output = "Pong : " + msg;
        return Response.status(200).entity(output).build();
    }
}

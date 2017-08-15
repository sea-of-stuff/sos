package uk.ac.standrews.cs.sos.rest.api;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos")
public class RESTGeneral {

    @GET
    @Path("/info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getInfo() {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/info");

        SOSLocalNode sos = RESTConfig.sos;
        return Response.status(HTTPStatus.OK)
                .entity(sos.toString())
                .build();
    }

}

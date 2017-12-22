package uk.ac.standrews.cs.sos.rest.api;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static uk.ac.standrews.cs.sos.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos")
public class RESTGeneral {

    @GET
    @Path("/ping/{msg: .*}") // Accept both empty and non-empty param
    public Response getMsg(@PathParam("msg") final String msg, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/ping/{msg}");

        String output = "Pong";
        if (msg != null && !msg.isEmpty()) {
            output += ": " + msg;
        }

        return HTTPResponses.OK(RESTConfig.sos, node_challenge, output);
    }

    @POST
    @Path("/payload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postMsg(final InputStream inputStream, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/ping/{msg}");

        return HTTPResponses.OK(RESTConfig.sos, node_challenge, "Data received");
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo(@HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/info");

        return HTTPResponses.OK(RESTConfig.sos, node_challenge, RESTConfig.sos.toString());
    }

    @GET
    @Path("/sign/{message}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSignedContent(@PathParam("message") final String message, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/sign/{message}");

        SOSLocalNode sos = RESTConfig.sos;
        try {
            String signedMessage = sos.sign(message);
            return HTTPResponses.OK(RESTConfig.sos, node_challenge, signedMessage);

        } catch (CryptoException e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }

    }

}

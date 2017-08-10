package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.RMSNode;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/usro/")
@RMSNode
public class RESTRMS {

    @GET
    @Path("/user/{guid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUser(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /user/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID userGUID;
        try {
            userGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        UsersRolesService usro = RESTConfig.sos.getRMS();

        try {
            String user = usro.getUser(userGUID).toString();
            return HTTPResponses.OK(user);

        } catch (UserNotFoundException e) {
            return HTTPResponses.NOT_FOUND("Could not find user with guid: " + userGUID.toMultiHash());
        }

    }

    @GET
    @Path("/role/{guid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRole(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /user/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID userGUID;
        try {
            userGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        UsersRolesService usro = RESTConfig.sos.getRMS();

        try {
            String role = usro.getRole(userGUID).toString();
            return HTTPResponses.OK(role);

        } catch (RoleNotFoundException e) {
            return HTTPResponses.NOT_FOUND("Could not find role with guid: " + userGUID.toMultiHash());
        }

    }

}

package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.RMSNode;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
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
@Path("/sos/usro/")
@RMSNode
public class RESTRMS {

    @GET
    @Path("/user/{guid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUser(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/usro/user/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID userGUID;
        try {
            userGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        UsersRolesService usro = RESTConfig.sos.getRMS();

        try {
            String user = usro.getUser(userGUID).toString();
            return HTTPResponses.OK(RESTConfig.sos, node_challenge, user);

        } catch (UserNotFoundException e) {
            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Could not find user with guid: " + userGUID.toMultiHash());
        }

    }

    @GET
    @Path("/role/{guid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRole(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/usro/role/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID userGUID;
        try {
            userGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        UsersRolesService usro = RESTConfig.sos.getRMS();

        try {
            String role = usro.getRole(userGUID).toString();
            return HTTPResponses.OK(RESTConfig.sos, node_challenge, role);

        } catch (RoleNotFoundException e) {
            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Could not find role with guid: " + userGUID.toMultiHash());
        }

    }

    @GET
    @Path("/user/{guid}/roles")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserRoles(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/usro/user/{guid}/roles");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID userGUID;
        try {
            userGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        UsersRolesService usro = RESTConfig.sos.getRMS();

        try {
            Set<Role> roles = usro.getRoles(userGUID);

            String out = JSONHelper.JsonObjMapper().writeValueAsString(roles);
            return HTTPResponses.OK(RESTConfig.sos, node_challenge, out);

        } catch (RoleNotFoundException | JsonProcessingException e) {
            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Could not find user with guid: " + userGUID.toMultiHash());
        }

    }

    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response postUser(final User user, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/usro/user");

        UsersRolesService usro = RESTConfig.sos.getRMS();

        try {
            usro.addUser(user);
            return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, "User with GUID " + user.guid().toMultiHash() + " added");

        } catch (UserRolePersistException e) {

            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

    @POST
    @Path("/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response postRole(final Role role, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/usro/role");

        UsersRolesService usro = RESTConfig.sos.getRMS();

        try {
            usro.addRole(role);
            return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, "Role with GUID " + role.guid().toMultiHash() + " added");

        } catch (UserRolePersistException e) {

            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

}

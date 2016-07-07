package uk.ac.standrews.cs.sos.rest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
//@Path("roles")
public class Roles {

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getRoles() throws GUIDGenerationException {
//        return getThisNodeRoles();
//    }

//    @GET
//    @Path("coordinator")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getRolesCoordinator(@QueryParam("guid") String input) throws GUIDGenerationException, SOSException {
//        IGUID guid = GUIDFactory.recreateGUID(input);
//        return getNodeRoles(guid);
//    }
//
//    private Response getThisNodeRoles() {
//        ROLE[] roles = ServerState.sos.getRoles();
//        String json = rolesToJsonArray(roles);
//
//        return Response
//                .status(Response.Status.ACCEPTED)
//                .entity(json)
//                .type(MediaType.APPLICATION_JSON_TYPE)
//                .build();
//    }
//
//    private Response getNodeRoles(IGUID guid) throws SOSException {
//        SeaOfStuff sos = ServerState.sos.getSeaOfStuff(ROLE.COORDINATOR);
//        Node node = sos.getNode(guid);
//
//        if (node != null) {
//            ROLE[] roles = node.getRoles();
//            String json = rolesToJsonArray(roles);
//
//            return Response
//                    .status(Response.Status.ACCEPTED)
//                    .entity(json)
//                    .type(MediaType.APPLICATION_JSON_TYPE)
//                    .build();
//
//        } else {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//    }
//
//    private String rolesToJsonArray(ROLE[] roles) {
//        ArrayNode node = JSONHelper.JsonObjMapper().createArrayNode();
//        for(ROLE role:roles) {
//            node.add(role.toString());
//        }
//
//        return node.toString();
//    }

}

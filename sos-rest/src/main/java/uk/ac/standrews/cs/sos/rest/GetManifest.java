package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.node.ROLE;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("manifest")
public class GetManifest {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManifestGivenGUID(@QueryParam("guid") String input) throws GUIDGenerationException, SOSException {
        IGUID guid = GUIDFactory.recreateGUID(input);
        Manifest manifest = null;
        try {
            manifest = ServerState.sos.getSeaOfStuff(ROLE.CLIENT).getManifest(guid); // FIXME - pass role in header of request ?
        } catch (ManifestNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(manifest.toJSON().toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getManifestGivenMetadata(@QueryParam("metadata") String metadata) throws GUIDGenerationException {
//        IGUID guid = GUIDFactory.recreateGUID(metadata);
//        Manifest manifest = null;
//        try {
//            manifest = ServerState.sos.getSOS(ROLE.CLIENT).getManifest(guid); // FIXME
//        } catch (ManifestNotFoundException e) {
//            return Response
//                    .status(Response.Status.NOT_FOUND)
//                    .build();
//        }
//
//        return Response
//                .status(Response.Status.ACCEPTED)
//                .entity(manifest.toJSON().toString())
//                .type(MediaType.APPLICATION_JSON_TYPE)
//                .build();
//    }

//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getManifestGivenMetadata() throws GUIDGenerationException {
        /*
        IGUID guid = GUIDFactory.recreateGUID(metadata);
        Manifest manifest = null;
        try {
            manifest = ServerState.sos.getManifest(guid); // FIXME
        } catch (ManifestNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(manifest.toJSON().toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
                */
//        return null;
//    }
}

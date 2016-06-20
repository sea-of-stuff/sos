package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
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
    @Path("coordinator")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManifestGivenGUIDCoordinator(@QueryParam("guid") String input) throws GUIDGenerationException, SOSException {
        return getManifestGivenGUID(ROLE.COORDINATOR, input);
    }

    @GET
    @Path("storage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManifestGivenGUIDStorage(@QueryParam("guid") String input) throws GUIDGenerationException, SOSException {
        return getManifestGivenGUID(ROLE.STORAGE, input);
    }

    public Response getManifestGivenGUID(ROLE role, @QueryParam("guid") String input) throws GUIDGenerationException, SOSException {
        IGUID guid = GUIDFactory.recreateGUID(input);
        Manifest manifest = null;
        SeaOfStuff sos = ServerState.sos.getSeaOfStuff(role);
        try {
            manifest = sos.getManifest(guid);
        } catch (ManifestNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(manifest.toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

}

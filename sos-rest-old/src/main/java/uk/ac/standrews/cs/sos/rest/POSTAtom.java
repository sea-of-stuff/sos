package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.node.ROLE;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("atom")
public class POSTAtom {

    @POST
    @Path("/location")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAtomLocations(String json) throws SOSException, IOException {

        JsonNode node = JSONHelper.JsonObjMapper().readTree(json);
        String uri = node.get(ManifestConstants.BUNDLE_LOCATION).textValue();

        Location location = null;
        try {
            if (uri.startsWith("sos")) {
                location = new SOSLocation(uri);
            } else {
                location = new URILocation(uri);
            }
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        Atom manifest = null;
        try {
            manifest = ServerState.sos.getSeaOfStuff(ROLE.CLIENT).addAtom(location);
        } catch (DataStorageException | ManifestPersistException e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(manifest.toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    @POST
    @Path("/stream")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAtomStream(final InputStream inputStream) throws SOSException {

        Atom manifest = null;
        try {
            manifest = ServerState.sos.getSeaOfStuff(ROLE.CLIENT).addAtom(inputStream);
        } catch (DataStorageException | ManifestPersistException e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(manifest.toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

}

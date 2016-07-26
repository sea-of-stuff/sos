package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;
import uk.ac.standrews.cs.sos.model.LocationModel;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/storage/")
public class RESTStorage {

    @GET
    @Path("/data/guid/{guid}")
    public Response getData(@PathParam("guid") String guid) {
        if (!ServerState.sos.isStorage()) {
            return Response.status(HTTPState.BAD_REQUEST)
                    .entity("Sorry, but I am not a storage node")
                    .build();
        }

        if (guid == null || guid.isEmpty()) {
            return Response.status(HTTPState.BAD_REQUEST)
                    .entity("Wrong input, sorry")
                    .build();
        }

        IGUID atomGUID;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return Response.status(HTTPState.BAD_REQUEST)
                    .entity("Wrong input, sorry")
                    .build();
        }

        Storage storage = ServerState.sos.getStorage();
        InputStream inputStream = storage.getAtomContent(atomGUID);

        return Response.status(HTTPState.OK)
                .entity(inputStream)
                .type(MediaType.MULTIPART_FORM_DATA) // Note - this is a general media-type. will not render on browser.
                .build();
    }

    @POST
    @Path("/uri")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDataByLocation(LocationModel locationModel) {
        if (!ServerState.sos.isStorage()) {
            return Response.status(HTTPState.BAD_REQUEST)
                    .entity("Sorry, but I am not a storage node")
                    .build();
        }

        Storage storage = ServerState.sos.getStorage();

        Location location;
        try {
            location = locationModel.getLocation();
        } catch (IOException e) {
            return Response.status(HTTPState.BAD_REQUEST)
                    .entity("Wrong input, sorry")
                    .build();
        }

        try {
            Atom atom = storage.addAtom(location);

            return Response.status(HTTPState.CREATED)
                    .entity(atom.toString())
                    .build();
        } catch (StorageException | ManifestPersistException e) {
            return Response.status(500).entity("Something went wrong on our side. Sorry").build();
        }

    }



}

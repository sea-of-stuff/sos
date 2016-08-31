package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.bindings.StorageNode;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;
import uk.ac.standrews.cs.sos.json.model.LocationModel;
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
@StorageNode
public class RESTStorage {

    @GET
    @Path("/data/guid/{guid}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getData(@PathParam("guid") String guid) {
        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID atomGUID;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        Storage storage = ServerState.sos.getStorage();
        InputStream inputStream = storage.getAtomContent(atomGUID);

        return HTTPResponses.OK(inputStream);
    }

    @POST
    @Path("/uri")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postDataByLocation(LocationModel locationModel) {
        Storage storage = ServerState.sos.getStorage();

        Location location;
        try {
            location = locationModel.getLocation();
        } catch (IOException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        try {
            Atom atom = storage.addAtom(location);

            return HTTPResponses.CREATED(atom.toString());
        } catch (StorageException | ManifestPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @POST
    @Path("/stream")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAtomStream(final InputStream inputStream) {
        Storage storage = ServerState.sos.getStorage();

        Atom manifest;
        try {
            manifest = storage.addAtom(inputStream);
        } catch (StorageException | ManifestPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        return HTTPResponses.CREATED(manifest.toString());
    }

}

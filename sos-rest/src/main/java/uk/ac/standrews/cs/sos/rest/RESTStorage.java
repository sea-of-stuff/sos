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
            return HTTPResponses.BAD_REQUEST("I am not a storage node");
        }

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDataByLocation(LocationModel locationModel) {
        if (!ServerState.sos.isStorage()) {
            return HTTPResponses.BAD_REQUEST("I am not a storage node");
        }

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



}

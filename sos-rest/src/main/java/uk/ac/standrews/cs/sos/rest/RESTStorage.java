package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.actors.Storage;
import uk.ac.standrews.cs.sos.bindings.StorageNode;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.json.model.LocationModel;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.protocol.DDSNotificationInfo;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

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
        SOS_LOG.log(LEVEL.INFO, "REST: GET /storage/data/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID atomGUID;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        Storage storage = RESTConfig.sos.getStorage();
        InputStream inputStream;
        try {
            inputStream = storage.getAtomContent(atomGUID);
        } catch (AtomNotFoundException e) {
            return HTTPResponses.NOT_FOUND("Atom not found");
        }

        return HTTPResponses.OK(inputStream);
    }

    @POST
    @Path("/uri")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDataByLocation(LocationModel locationModel) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /storage/uri");

        if (locationModel == null) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        Storage storage = RESTConfig.sos.getStorage();

        Location location;
        try {
            location = locationModel.getLocation();
        } catch (IOException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        try {
            AtomBuilder builder = new AtomBuilder().setLocation(location);
            Atom atom = storage.addAtom(builder, true, new DDSNotificationInfo());

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
        SOS_LOG.log(LEVEL.INFO, "REST: POST /storage/stream");

        Storage storage = RESTConfig.sos.getStorage();

        Atom atom;
        try {
            AtomBuilder builder = new AtomBuilder().setInputStream(inputStream);
            atom = storage.addAtom(builder, true, new DDSNotificationInfo()
                    .setNotifyDDSNodes(true)
                    .setUseDefaultDDSNodes(true)); // TODO - must be configurable from config file

        } catch (StorageException | ManifestPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        return HTTPResponses.CREATED(atom.toString());
    }

}

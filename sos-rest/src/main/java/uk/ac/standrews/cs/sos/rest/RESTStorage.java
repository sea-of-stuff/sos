package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.StorageNode;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.SecureAtom;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/storage/")
@StorageNode
public class RESTStorage {

    /**
     * Get the data as a stream of bytes
     *
     * @param guid matching the Atom of the data
     * @return a response with a body containing a stream of bytes
     */
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

        try (Data data = storage.getAtomContent(atomGUID)){

            return HTTPResponses.OK(data.getInputStream());

        } catch (Exception e) {
            return HTTPResponses.NOT_FOUND("Atom not found");
        }

    }

    /**
     * Example of body:
     *
     * { "location": "http://image.flaticon.com/teams/new/1-freepik.jpg"}
     * @param location JSON for URI
     * @return Response to the HTTP request
     */
    @POST
    @Path("/uri/replicas/{replicas}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDataByLocation(final Location location, @PathParam("replicas") int replicas) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /storage/uri");

        if (location == null) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        if (replicas < 0 || replicas > 3 /* TODO - this should be based on some settings */) {
            return HTTPResponses.BAD_REQUEST("The replicas parameter is invalid");
        }

        Storage storage = RESTConfig.sos.getStorage();

        try {
            AtomBuilder builder = new AtomBuilder()
                    .setLocation(location)
                    .setBundleType(BundleTypes.PERSISTENT);
            Atom atom = storage.addAtom(builder);

            return HTTPResponses.CREATED(atom.toString());
        } catch (DataStorageException | ManifestPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    /**
     * Add an atom to the SOS node
     *
     * @param inputStream the bytes of the atom
     * @return the Response to the http request
     */
    @POST
    @Path("/stream/replicas/{replicas}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAtomStream(final InputStream inputStream, @PathParam("replicas") int replicas) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /storage/stream");

        if (replicas < 0 || replicas > 3 /* TODO - this should be based on some settings */) {
            return HTTPResponses.BAD_REQUEST("The replicas parameter is invalid");
        }

        Storage storage = RESTConfig.sos.getStorage();

        try {
            AtomBuilder builder = new AtomBuilder()
                    .setData(new InputStreamData(inputStream))
                    .setBundleType(BundleTypes.PERSISTENT);
            Atom atom = storage.addAtom(builder); // TODO - pass replication factor here

            return HTTPResponses.CREATED(atom.toString());

        } catch (DataStorageException | ManifestPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @POST
    @Path("/stream/protected")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSecureAtomStream(final InputStream inputStream /* final String role  rolemodel*/) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /storage/stream");

        Storage storage = RESTConfig.sos.getStorage();

        SecureAtom atom;
        try {
            AtomBuilder builder = new AtomBuilder()
                    .setData(new InputStreamData(inputStream))
                    .setBundleType(BundleTypes.PERSISTENT)
                    .setRole(null); // FIXME

            atom = storage.addSecureAtom(builder);

        } catch (DataStorageException | ManifestPersistException | ManifestNotMadeException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        return HTTPResponses.CREATED(atom.toString());
    }

    // TODO
    // GET /protect/guid - maybe in dds? not sure


}

package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.SecureAtom;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.StorageNode;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/storage/")
@StorageNode
public class RESTStorage {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStorageInfo() {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/storage/info");

        try {
            SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings = SOSLocalNode.settings.getServices().getStorage();

            String storageInfo = JSONHelper.JsonObjMapper()
                    .writerWithView(SettingsConfiguration.Views.Public.class)
                    .writeValueAsString(storageSettings);

            return HTTPResponses.OK(storageInfo);

        } catch (JsonProcessingException e) {

            return HTTPResponses.INTERNAL_SERVER();
        }
    }

    // TODO - replica with suggested nodes


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
     * Add an atom to the SOS node
     *
     * @param inputStream the bytes of the atom
     * @return the Response to the http request
     */
    @POST
    @Path("/stream")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAtomStream(final InputStream inputStream) {
        return addAtomStream(inputStream, 1);
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

        if (replicas < 1 || replicas > 3 /* TODO - this should be based on some settings */) {
            return HTTPResponses.BAD_REQUEST("The replicas parameter is invalid");
        }


        try {
            AtomBuilder builder = new AtomBuilder()
                    .setData(new InputStreamData(inputStream))
                    .setBundleType(BundleTypes.PERSISTENT);

            Storage storage = RESTConfig.sos.getStorage();
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

        try {
            AtomBuilder builder = new AtomBuilder()
                    .setData(new InputStreamData(inputStream))
                    .setBundleType(BundleTypes.PERSISTENT)
                    .setRole(null); // FIXME

            Storage storage = RESTConfig.sos.getStorage();
            SecureAtom atom = storage.addSecureAtom(builder);
            return HTTPResponses.CREATED(atom.toString());

        } catch (DataStorageException | ManifestPersistException | ManifestNotMadeException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }


    }

    // TODO
    // GET /protect/guid - maybe in dds? not sure

    @GET
    @Path("/replicas/guid/{guid}")
    public Response getReplicasInfo(@PathParam("guid") final String guid) {

        /*
        {
        "guid" : guid,
        "processed" : [
         loc1, loc2, etc
        ],
        "toprocess": 2 // Location does not matter
        }
         */

        return HTTPResponses.INTERNAL_SERVER();
    }

    @GET
    @Path("/data/guid/{guid}/challenge/{challenge: .*}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getData(@PathParam("guid") final String guid, @PathParam("challenge") final String challenge) {

        SOS_LOG.log(LEVEL.INFO, "REST: GET /data/guid/" + guid + "/challenge/" + challenge);

        IGUID atomGUID;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        if (challenge.trim().isEmpty()) return HTTPResponses.BAD_REQUEST("Challenge is empty");

        Storage storage = RESTConfig.sos.getStorage();
        IGUID challengeResult = storage.challenge(atomGUID, challenge);

        return HTTPResponses.OK(challengeResult.toMultiHash());
    }
}

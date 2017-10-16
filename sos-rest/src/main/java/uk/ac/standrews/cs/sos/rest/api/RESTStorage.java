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
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.json.DataPackage;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.SecureAtom;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.StorageNode;
import uk.ac.standrews.cs.sos.services.StorageService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

import static uk.ac.standrews.cs.sos.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/storage/")
@StorageNode
public class RESTStorage {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStorageInfo(@HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/storage/info");

        try {
            SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings = SOSLocalNode.settings.getServices().getStorage();

            String storageInfo = JSONHelper.JsonObjMapper()
                    .writerWithView(SettingsConfiguration.Views.Public.class)
                    .writeValueAsString(storageSettings);

            return HTTPResponses.OK(RESTConfig.sos, node_challenge, storageInfo);

        } catch (JsonProcessingException e) {

            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }
    }

    /**
     * Get the data as a stream of bytes
     *
     * @param guid matching the Atom of the data
     * @return a response with a body containing a stream of bytes
     */
    @GET
    @Path("/data/guid/{guid}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getData(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/storage/data/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID atomGUID;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        StorageService storageService = RESTConfig.sos.getStorageService();
        try (Data data = storageService.getAtomContent(atomGUID)){

            return HTTPResponses.OK(RESTConfig.sos, node_challenge, data.getInputStream());

        } catch (Exception e) {
            return HTTPResponses.NOT_FOUND(RESTConfig.sos, node_challenge, "Atom not found");
        }

    }

    // TODO - ignore if data is already stored in node?
    /**
     * Add an atom to the SOS node
     *
     * @param dataPackage the bytes of the atom
     * @return the Response to the http request
     */
    @POST
    @Path("/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAtomStream(final DataPackage dataPackage, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/storage/stream");

        try {
            IGUID guidOfReceivedData = GUIDFactory.generateGUID(dataPackage.getDataObj().getInputStream());
            if (!guidOfReceivedData.equals(dataPackage.getGUIDObj())) {
                return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Data received does not match the GUID");
            }

            StorageService storageService = RESTConfig.sos.getStorageService();

            AtomBuilder builder = new AtomBuilder()
                    .setData(dataPackage.getDataObj())
                    .setBundleType(BundleTypes.PERSISTENT);

            if (dataPackage.getMetadata() != null) {

                int replicationFactor = dataPackage.getMetadata().getReplicationFactor();

                if (replicationFactor < 1 || replicationFactor > storageService.getStorageSettings().getMaxReplication()) {
                    return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "The replicas parameter is invalid");
                }

                builder = builder.setReplicationNodes(dataPackage.getMetadata().getReplicationNodes().getNodesCollection())
                        .setReplicationFactor(replicationFactor)
                        .setDelegateReplication(true); // This will be ignored anyway if replicas == 1
            }

            Atom atom = storageService.addAtom(builder);

            return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, atom.toString());

        } catch (DataStorageException | ManifestPersistException | NodesCollectionException | GUIDGenerationException | IOException e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }

    }

    @POST
    @Path("/stream/protected")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSecureAtomStream(final InputStream inputStream /* final String role  rolemodel*/, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) { // TODO - see add data with metadata method above
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/storage/stream");

        try {
            AtomBuilder builder = new AtomBuilder()
                    .setData(new InputStreamData(inputStream))
                    .setBundleType(BundleTypes.PERSISTENT)
                    .setRole(null); // FIXME

            StorageService storageService = RESTConfig.sos.getStorageService();
            SecureAtom atom = storageService.addSecureAtom(builder);
            return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, atom.toString());

        } catch (DataStorageException | ManifestPersistException | ManifestNotMadeException e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }


    }

    // TODO
    // GET /protect/guid - maybe in dds? not sure

    @GET
    @Path("/replicas/guid/{guid}")
    public Response getReplicasInfo(@PathParam("guid") final String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {

        /*
        {
        "guid" : guid,
        "processed" : [
         loc1, loc2, etc
        ],
        "toprocess": 2 // Location does not matter
        }
         */

        return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
    }

    @GET
    @Path("/data/guid/{guid}/challenge/{challenge: .*}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getData(@PathParam("guid") final String guid, @PathParam("challenge") final String challenge, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {

        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/storage/data/guid/" + guid + "/challenge/" + challenge);

        IGUID atomGUID;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        if (challenge.trim().isEmpty()) return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Challenge is empty");

        StorageService storageService = RESTConfig.sos.getStorageService();
        IGUID challengeResult = storageService.challenge(atomGUID, challenge);

        return HTTPResponses.OK(RESTConfig.sos, node_challenge, challengeResult.toMultiHash());
    }
}

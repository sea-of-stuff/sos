package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.DDSNode;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static uk.ac.standrews.cs.sos.impl.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/dds/")
@DDSNode
public class RESTDDS {

    @POST
    @Path("/manifest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postManifest(String json, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/dds/manifest");

        Manifest manifest;
        try {
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(json);
            ManifestType type = ManifestType.get(jsonNode.get(JSONConstants.KEY_TYPE).textValue());
            manifest = getManifest(type, json);
        } catch (IOException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }

        if (manifest == null) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }

        try {
            DataDiscoveryService dataDiscoveryService = RESTConfig.sos.getDDS();
            dataDiscoveryService.addManifest(manifest);

        } catch (ManifestPersistException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }

        return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, manifest.toString());
    }

    @GET
    @Path("/manifest/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManifest(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/dds/manifest/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID manifestGUID;
        try {
            manifestGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        try {
            DataDiscoveryService dataDiscoveryService = RESTConfig.sos.getDDS();
            Manifest manifest = dataDiscoveryService.getManifest(manifestGUID);
            return HTTPResponses.OK(RESTConfig.sos, node_challenge, manifest.toString());

        } catch (ManifestNotFoundException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }
    }

    @GET
    @Path("/manifest/guid/{guid}/challenge/{challenge: .*}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManifest(@PathParam("guid") String guid, @PathParam("challenge") final String challenge, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {

        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/dds/manifest/guid/" + guid + "/challenge/" + challenge);

        IGUID manifestGUID;
        try {
            manifestGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        if (challenge.trim().isEmpty()) return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Challenge is empty");

        DataDiscoveryService dds = RESTConfig.sos.getDDS();
        IGUID challengeResult = dds.challenge(manifestGUID, challenge);

        return HTTPResponses.OK(RESTConfig.sos, node_challenge, challengeResult.toMultiHash());
    }

    @POST
    @Path("/compound/data")
    public Response makeCompoundData() {

        // return storage node that should be used to upload the data
        // then should make a call to /compound/data/finalise (containing json of all components)
        // storage node should have the "freedom" to delete the data until finalise call
        return null;
    }

    private Manifest getManifest(ManifestType type, String json) throws IOException {
        Manifest manifest;
        switch(type) {
            case ATOM:
                manifest = getAtomManifest(json);
                break;
            case COMPOUND:
                manifest = getCompoundManifest(json);
                break;
            case VERSION:
                manifest = getVersionManifest(json);
                break;
            default:
                return null;
        }

        return manifest;
    }

    private AtomManifest getAtomManifest(String json) throws IOException {
        return JSONHelper.JsonObjMapper().readValue(json, AtomManifest.class);
    }

    private CompoundManifest getCompoundManifest(String json) throws IOException {
        return JSONHelper.JsonObjMapper().readValue(json, CompoundManifest.class);
    }

    private VersionManifest getVersionManifest(String json) throws IOException {
        return JSONHelper.JsonObjMapper().readValue(json, VersionManifest.class);
    }
}

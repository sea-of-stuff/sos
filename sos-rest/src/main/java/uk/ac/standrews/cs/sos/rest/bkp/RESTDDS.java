package uk.ac.standrews.cs.sos.rest.bkp;

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

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/dds/")
@DDSNode
public class RESTDDS {

    @POST
    @Path("/manifest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postManifest(String json) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /dds/manifest");

        Manifest manifest;
        try {
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(json);
            ManifestType type = ManifestType.get(jsonNode.get(JSONConstants.KEY_TYPE).textValue());
            manifest = getManifest(type, json);
        } catch (IOException e) {
            return HTTPResponses.BAD_REQUEST("Invalid Input");
        }

        if (manifest == null) {
            return HTTPResponses.BAD_REQUEST("Invalid Input");
        }

        try {
            DataDiscoveryService dataDiscoveryService = RESTConfig.sos.getDDS();
            dataDiscoveryService.addManifest(manifest);

        } catch (ManifestPersistException e) {
            return HTTPResponses.BAD_REQUEST("Invalid Input");
        }

        return HTTPResponses.CREATED(manifest.toString());
    }

    @GET
    @Path("/manifest/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManifest(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /dds/manifest/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID manifestGUID;
        try {
            manifestGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        try {
            DataDiscoveryService dataDiscoveryService = RESTConfig.sos.getDDS();
            Manifest manifest = dataDiscoveryService.getManifest(manifestGUID);
            return HTTPResponses.OK(manifest.toString());

        } catch (ManifestNotFoundException e) {
            return HTTPResponses.BAD_REQUEST("Invalid Input");
        }
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

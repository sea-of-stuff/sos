package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.bindings.DDSNode;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

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

        Manifest manifest;
        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(json);
            String type = node.get(ManifestConstants.KEY_TYPE).textValue();
            manifest = getManifest(type, json);
        } catch (IOException e) {
            return HTTPResponses.BAD_REQUEST("Invalid Input");
        }

        if (manifest == null) {
            return HTTPResponses.BAD_REQUEST("Invalid Input");
        }

        return HTTPResponses.CREATED(manifest.toString());
    }

    @GET
    @Path("/manifest/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManifest(@PathParam("guid") String guid) {

        return HTTPResponses.OK("Test");
    }

    private Manifest getManifest(String type, String json) throws IOException {
        Manifest manifest;
        switch(type) {
            case ManifestConstants.ATOM:
                manifest = getAtomManifest(json);
                break;
            case ManifestConstants.COMPOUND:
                manifest = getCompoundManifest(json);
                break;
            case ManifestConstants.VERSION:
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

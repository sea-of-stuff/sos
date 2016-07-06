package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.json.CommonJson;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.node.ROLE;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("version")
public class POSTVersion {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVersion(String json) throws GUIDGenerationException, SOSException, IOException {

        JsonNode tree = JSONHelper.JsonObjMapper().readTree(json);

        IGUID invariant = null;
        if (tree.has(ManifestConstants.KEY_INVARIANT)) {
            invariant = GUIDFactory.recreateGUID(tree.get(ManifestConstants.KEY_INVARIANT).textValue());
        }

        IGUID content = null;
        if (tree.has(ManifestConstants.KEY_CONTENT_GUID)) {
            content = GUIDFactory.recreateGUID(tree.get(ManifestConstants.KEY_CONTENT_GUID).textValue());
        }

        Collection<IGUID> prevs = CommonJson.GetGUIDCollection(tree, ManifestConstants.KEY_PREVIOUS_GUID);
        Collection<IGUID> metadata = CommonJson.GetGUIDCollection(tree, ManifestConstants.KEY_METADATA_GUID);

        Version version;
        try {
            version = ServerState.sos.getSeaOfStuff(ROLE.CLIENT).addVersion(content, invariant, prevs, metadata);
        } catch (ManifestNotMadeException | ManifestPersistException e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(version.toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

}

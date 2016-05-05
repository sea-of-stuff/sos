package uk.ac.standrews.cs.sos.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("version")
public class POSTVersion {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVersion(String json) throws GUIDGenerationException {

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        IGUID invariant = null;
        IGUID content = null;

        if (jsonObject.has(ManifestConstants.KEY_INVARIANT)) {
            invariant = GUIDFactory.recreateGUID(jsonObject.get(ManifestConstants.KEY_INVARIANT).getAsString());
        }

        if (jsonObject.has(ManifestConstants.KEY_CONTENT_GUID)) {
            content = GUIDFactory.recreateGUID(jsonObject.get(ManifestConstants.KEY_CONTENT_GUID).getAsString());
        }

        JsonArray jsonPrevsArray = jsonObject.getAsJsonArray(ManifestConstants.KEY_PREVIOUS_GUID);
        Collection<IGUID> prevs = new ArrayList<>();
        for (int i = 0; i < jsonPrevsArray.size(); i++) {
            IGUID guid = GUIDFactory.recreateGUID(jsonPrevsArray.get(i).getAsString());
            prevs.add(guid);
        }

        JsonArray jsonMetaArray = jsonObject.getAsJsonArray(ManifestConstants.KEY_METADATA_GUID);
        Collection<IGUID> metadata = new ArrayList<>();
        for (int i = 0; i < jsonMetaArray.size(); i++) {
            IGUID guid = GUIDFactory.recreateGUID(jsonMetaArray.get(i).getAsString());
            metadata.add(guid);
        }

        Compound manifest = null;
        try {
            ServerState.sos.addVersion(content, invariant, prevs, metadata);
        } catch (ManifestNotMadeException | ManifestPersistException e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(manifest.toJSON().toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}

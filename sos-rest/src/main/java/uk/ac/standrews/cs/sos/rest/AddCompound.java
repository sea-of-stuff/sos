package uk.ac.standrews.cs.sos.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;

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
@Path("add/compound")
public class AddCompound {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCompound(String json) {

        JsonParser parser = new JsonParser();
        JsonArray jsonContentArray = parser.parse(json).getAsJsonArray();
        Collection<Content> contents = new ArrayList<>();
        for (int i = 0; i < jsonContentArray.size(); i++) {
            Content content = ServerState.gson.fromJson(jsonContentArray.get(i), Content.class);
            contents.add(content);
        }

        Compound manifest = null;
        try {
            manifest = ServerState.sos.addCompound(CompoundType.COLLECTION, contents); // TODO - allow also other types of compounds
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

package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.node.ROLE;
import uk.ac.standrews.cs.sos.utils.Helper;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("compound")
public class POSTCompound {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCompound(String json) throws SOSException, IOException {

        JsonNode tree = Helper.JsonObjMapper().readTree(json);
        assert tree.isArray();

        Collection<Content> contents = new ArrayList<>();
        for(final JsonNode node:tree) {
            Content content = Helper.JsonObjMapper().convertValue(node, Content.class);
            contents.add(content);
        }

        Compound manifest = null;
        try {
            manifest = ServerState.sos.getSeaOfStuff(ROLE.CLIENT).addCompound(CompoundType.COLLECTION, contents); // TODO - allow also other types of compounds
        } catch (ManifestNotMadeException | ManifestPersistException e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(manifest.toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

}

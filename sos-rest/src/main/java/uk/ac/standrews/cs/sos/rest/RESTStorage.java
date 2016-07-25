package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/storage")
public class RESTStorage {

    @GET
    @Path("/data/{guid}")
    public Response getData(@PathParam("guid") String guid) {
        if (!ServerState.sos.isStorage()) {
            return Response.status(400).entity("Sorry, but I am not a storage node").build();
        }

        if (guid == null || guid.isEmpty()) {
            return Response.status(400).entity("Wrong input, sorry").build();
        }

        IGUID atomGUID = null;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return Response.status(400).entity("Wrong input, sorry").build();
        }

        Storage storage = ServerState.sos.getStorage();
        InputStream inputStream = storage.getAtomContent(atomGUID);

        return Response.status(200)
                .entity(inputStream)
                .type(MediaType.MULTIPART_FORM_DATA) // Note - this is a general media-type. will not render on browser.
                .build();
    }
}

package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("atom")
public class GetAtom {

    @GET
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getAtom(@QueryParam("guid") String input) throws GUIDGenerationException {
        IGUID guid = GUIDFactory.recreateGUID(input);

        // ServerState.sos.getAtomContent(guid);
        // TODO - return atom stream
        return null;
    }

}

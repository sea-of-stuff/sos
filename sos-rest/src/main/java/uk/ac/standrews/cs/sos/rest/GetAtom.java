package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.node.ROLE;
import uk.ac.standrews.cs.sos.node.SOS.SOSClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("atom")
public class GetAtom {

    @GET
    @Path("client")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getAtom(@QueryParam("guid") String input) throws GUIDGenerationException, ManifestNotFoundException {
        IGUID guid = GUIDFactory.recreateGUID(input);

        SOSClient sos = (SOSClient) ServerState.sos.getSOS(ROLE.CLIENT);

        Atom atom = (Atom) sos.getManifest(guid);
        InputStream stream = sos.getAtomContent(atom);

        return Response.ok()
                .entity(stream)
                .type(MediaType.MULTIPART_FORM_DATA) // TODO - this is a general media-type. will not render on browser.
                .build();
    }

}

package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.MCSNode;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.interfaces.actors.MCS;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/mcs/")
@MCSNode
public class RESTMCS {

    @POST
    @Path("/process")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processMetadata(final InputStream inputStream) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /process");

        MCS mcs = RESTConfig.sos.getMCS();

        SOSMetadata metadata;
        try {
            metadata = mcs.processMetadata(inputStream);
        } catch (MetadataException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        return HTTPResponses.OK(metadata.toString());
    }

}

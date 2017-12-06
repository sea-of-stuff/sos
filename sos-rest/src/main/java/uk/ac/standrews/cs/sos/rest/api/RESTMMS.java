package uk.ac.standrews.cs.sos.rest.api;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.MMSNode;
import uk.ac.standrews.cs.sos.services.MetadataService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

import static uk.ac.standrews.cs.sos.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/mms/")
@MMSNode
public class RESTMMS {

    @POST
    @Path("/metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postMetadata(String json, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/mms/metadata");

        Metadata metadata = JSONHelper.JsonObjMapper().readValue(json, Metadata.class);

        MetadataService metadataService = RESTConfig.sos.getMMS();
        try {
            metadataService.addMetadata(metadata);
            return HTTPResponses.CREATED(RESTConfig.sos, node_challenge, metadata.toString());

        } catch (MetadataPersistException e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }


    }

    @GET
    @Path("/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getMetadata(@PathParam("guid") String guid, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/mms/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        IGUID metadataGUID;
        try {
            metadataGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Bad input");
        }

        MetadataService metadataService = RESTConfig.sos.getMMS();
        try {
            Metadata metadata = metadataService.getMetadata(metadataGUID);
            return HTTPResponses.OK(RESTConfig.sos, node_challenge, metadata.toString());
        } catch (MetadataNotFoundException e) {
            return HTTPResponses.BAD_REQUEST(RESTConfig.sos, node_challenge, "Invalid Input");
        }
    }

    @POST
    @Path("/process")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processMetadata(final InputStream inputStream, @HeaderParam(SOS_NODE_CHALLENGE_HEADER) String node_challenge) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /sos/mms/process");

        MetadataService MetadataService = RESTConfig.sos.getMMS();

        try {
            Data data = new InputStreamData(inputStream);
            MetadataBuilder metadataBuilder = new MetadataBuilder().setData(data); // FIXME - maybe role is passed as param ?
            Metadata metadata = MetadataService.processMetadata(metadataBuilder);

            return HTTPResponses.OK(RESTConfig.sos, node_challenge, metadata.toString());
        } catch (MetadataException e) {
            return HTTPResponses.INTERNAL_SERVER(RESTConfig.sos, node_challenge);
        }


    }

}
